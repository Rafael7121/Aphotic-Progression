from pathlib import Path
import hashlib, json, os, shutil, subprocess, sys, tempfile, zipfile

OUT = Path(__file__).resolve().parent
ROOT = OUT.parents[1]
LIB = Path('C:/Users/rafae/curseforge/minecraft/Install/libraries')
BUILD = OUT / 'build'
BUILD.mkdir(exist_ok=True)
CLASSES = BUILD / 'classes'
CLASSES.mkdir(exist_ok=True)
RESOURCES = OUT / 'src/main/resources'
JAR = BUILD / 'apothic-progression-1.21.1-1.0.1.jar'
deps = [
    LIB / "net/neoforged/neoforge/21.1.249/neoforge-21.1.249-client.jar",
    LIB / "net/neoforged/neoforge/21.1.249/neoforge-21.1.249-universal.jar",
    OUT / "mixinextras-neoforge-0.5.3.jar",
    ROOT / "mods/ApothicEnchanting-1.21.1-1.6.1.jar",
    LIB / 'net/minecraft/client/1.21.1-20240808.144430/client-1.21.1-20240808.144430-srg.jar',
    LIB / 'net/minecraft/client/1.21.1-20240808.144430/client-1.21.1-20240808.144430-extra.jar',
    LIB / 'net/neoforged/fancymodloader/loader/4.0.44/loader-4.0.44.jar',
    LIB / 'net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar',
    LIB / 'com/google/code/gson/gson/2.14.0/gson-2.14.0.jar',
    LIB / 'com/mojang/datafixerupper/8.0.16/datafixerupper-8.0.16.jar',
    LIB / 'com/mojang/brigadier/1.3.10/brigadier-1.3.10.jar',
    LIB / 'it/unimi/dsi/fastutil/8.5.18/fastutil-8.5.18.jar',
    LIB / 'org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar',
    LIB / 'org/joml/joml/1.10.8/joml-1.10.8.jar',
    LIB / 'com/mojang/logging/1.7.12/logging-1.7.12.jar',
    LIB / 'com/mojang/authlib/6.0.54/authlib-6.0.54.jar',
    LIB / 'org/apache/commons/commons-lang3/3.20.0/commons-lang3-3.20.0.jar',
    LIB / 'commons-io/commons-io/2.20.0/commons-io-2.20.0.jar',
    LIB / 'com/google/guava/guava/33.6.0-jre/guava-33.6.0-jre.jar',
    LIB / 'org/apache/logging/log4j/log4j-api/2.26.0/log4j-api-2.26.0.jar',
    LIB / 'org/ow2/asm/asm/9.10.1/asm-9.10.1.jar',
    LIB / 'org/ow2/asm/asm-tree/9.10.1/asm-tree-9.10.1.jar',
    ROOT / 'mods/Placebo-1.21.1-9.9.2.jar',
]
deps.extend(sorted((LIB/'io/netty').glob('*/4.2.15.Final/*.jar')))
assert all(p.exists() for p in deps)
cp = os.pathsep.join(map(str, deps))
def run(command):
    p = subprocess.run(command, text=True, capture_output=True)
    if p.stdout: print(p.stdout.strip())
    if p.stderr: print(p.stderr.strip())
    p.check_returncode()
    return p.stdout

sources = list((OUT/'src/main/java').rglob('*.java'))
run(['javac','--release','21','-proc:none','-cp',cp,'-d',str(CLASSES), *map(str,sources)])
run(['jar','--create','--file',str(JAR),'-C',str(CLASSES),'.','-C',str(RESOURCES),'.'])

tests=BUILD/'tests'
tests.mkdir(exist_ok=True)
testcp=os.pathsep.join([str(JAR),cp,str(tests)])
run(['javac','--release','21','-proc:none','-cp',testcp,'-d',str(tests),*map(str,(OUT/'tests').glob('*.java'))])
result=run(['java','-Xmx256m','-cp',testcp,'ProgressionTest'])
(BUILD/'test-results.txt').write_text(result)
# Use vanilla text classes for the standalone wrapping check; the patched
# TranslatableContents requires a running FML environment.
result=run(['java','-Xmx256m','-cp',os.pathsep.join([str(deps[4]),testcp]),'XpBarTest'])
(BUILD/'xp-bar-results.txt').write_text(result)
result=run(['java','-Xmx768m','-cp',testcp,'ValidateTargets',str(JAR),str(deps[0]),str(deps[4]),str(ROOT/'mods/ApothicEnchanting-1.21.1-1.6.1.jar'),str(ROOT/'mods/EnchantingInfuser-v21.1.4-1.21.1-NeoForge.jar'),str(ROOT/'mods/Placebo-1.21.1-9.9.2.jar')])
(BUILD/'target-results.txt').write_text(result)
print('BUILT',JAR)
print('SHA256',hashlib.sha256(JAR.read_bytes()).hexdigest())
