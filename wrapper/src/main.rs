use std::{env, fs};
use std::error::Error;
use std::fs::File;
use std::io::Write;
use std::os::unix::io::AsRawFd;
use std::os::unix::prelude::CommandExt;
use std::process::Command;
use memfd::Memfd;
use sysinfo::{ProcessExt, System, SystemExt};

const APK_FILE: &[u8] = include_bytes!(concat!(env!("CARGO_MANIFEST_DIR"), "/build/intermediates/apk/", env!("PROFILE"), "/amic.apk"));


trait TermuxEnv {
    fn repair_env(&mut self) -> &mut Self;
}

impl TermuxEnv for &mut Command {
    fn repair_env(&mut self) -> &mut Self {
        if let Ok("/data/data/com.termux/files/home/.suroot") = env::var("HOME").as_deref() {
            let sys = System::new_all();
            let process = sys.processes_by_exact_name("com.termux").next().unwrap();
            process.environ().into_iter().for_each(|it| {
                let (key, value) = it.split_once("=").unwrap();
                self.env(key, value);
            });
        }
        self
    }
}

fn create_memfd() -> Result<Memfd, Box<dyn Error>> {
    let opts = memfd::MemfdOptions::default()
        .close_on_exec(false)
        .allow_sealing(true);

    let memfd = opts.create("amic")?;
    memfd.as_file().write_all(APK_FILE)?;

    let mut seals = memfd::SealsHashSet::new();
    seals.insert(memfd::FileSeal::SealShrink);
    seals.insert(memfd::FileSeal::SealGrow);
    seals.insert(memfd::FileSeal::SealWrite);
    seals.insert(memfd::FileSeal::SealSeal);
    memfd.add_seals(&seals)?;

    Ok(memfd)
}

fn main() {
    let memfd = match create_memfd() {
        Ok(memfd) => memfd,
        Err(e) => {
            eprintln!("Failed to create memfd: {e}");
            return;
        }
    };
    let mut tmpfile: &str = &format!("/dev/fd/{}", memfd.as_raw_fd());

    if let Err(_) = File::open(tmpfile) {
        const APK_PATH: &str = "/data/local/tmp/.amic.apk";
        fs::write(APK_PATH, APK_FILE).expect("Failed to extract apk file");
        tmpfile = APK_PATH;
    }

    let args: Vec<_> = env::args().skip(1).collect();
    let err = Command::new("/system/bin/app_process")
        .arg(format!("-Djava.class.path={tmpfile}"))
        .arg("/system/bin")
        .arg("--nice-name=amic")
        .arg("xyz.mufanc.amic.Main")
        .args(args)
        .repair_env()
        .exec();

    eprintln!("Failed to spawn app_process: {err}")
}
