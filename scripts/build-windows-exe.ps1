$ErrorActionPreference = "Stop"

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$outDir = Join-Path $root "dist"
$inputDir = Join-Path $outDir ".jpackage-input"
$zipPath = Join-Path $outDir "RedisChatroom-portable.zip"
$appName = "RedisChatroom"
$launcherSrc = Join-Path $root "packaging\launcher\src"
$launcherBuild = Join-Path $root "packaging\launcher\build"
$launcherJar = Join-Path $root "packaging\launcher\redis-chatroom-launcher.jar"
$mainJar = Join-Path $root "target\redis-chatroom-prototype-1.0-SNAPSHOT.jar"

if (-not (Test-Path $mainJar)) {
    throw "Missing jar: $mainJar. Run mvn clean package first."
}

$javaHome = $env:JAVA_HOME
if ([string]::IsNullOrWhiteSpace($javaHome)) {
    $fallbackJavaHome = "C:\Program Files\Java\jdk-17"
    if (Test-Path $fallbackJavaHome) {
        $javaHome = $fallbackJavaHome
    } else {
        throw "JAVA_HOME is not set and the fallback JDK path does not exist. Point it to a JDK 17 installation before packaging."
    }
}

if (Test-Path $launcherBuild) {
    Remove-Item -LiteralPath $launcherBuild -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $launcherBuild | Out-Null

$javac = Join-Path $javaHome "bin\javac.exe"
$jarTool = Join-Path $javaHome "bin\jar.exe"
if (-not (Test-Path $javac)) {
    throw "Missing javac: $javac"
}
if (-not (Test-Path $jarTool)) {
    throw "Missing jar tool: $jarTool"
}

$launcherSources = Get-ChildItem -Path $launcherSrc -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$javacArgs = @("-encoding", "UTF-8", "-d", $launcherBuild) + $launcherSources
& $javac @javacArgs
if ($LASTEXITCODE -ne 0) {
    throw "Launcher compilation failed with exit code $LASTEXITCODE"
}

if (Test-Path $launcherJar) {
    Remove-Item -LiteralPath $launcherJar -Force
}
& $jarTool --create --file $launcherJar -C $launcherBuild .
if ($LASTEXITCODE -ne 0) {
    throw "Launcher jar creation failed with exit code $LASTEXITCODE"
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
if (Test-Path $inputDir) {
    Remove-Item -LiteralPath $inputDir -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $inputDir | Out-Null
Copy-Item -LiteralPath $mainJar -Destination $inputDir
if (Test-Path $launcherJar) {
    Copy-Item -LiteralPath $launcherJar -Destination $inputDir
} else {
    throw "Missing launcher jar: $launcherJar"
}

if (Test-Path (Join-Path $outDir $appName)) {
    Remove-Item -LiteralPath (Join-Path $outDir $appName) -Recurse -Force
}
if (Test-Path $zipPath) {
    Remove-Item -LiteralPath $zipPath -Force
}

jpackage `
    --type app-image `
    --name $appName `
    --input $inputDir `
    --main-jar (Split-Path $launcherJar -Leaf) `
    --main-class com.chatroom.packaging.RedisChatroomDesktopLauncher `
    --dest $outDir `
    --app-version 1.0.0 `
    --win-console `
    --java-options "-Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE"
}

Compress-Archive -Path (Join-Path $outDir $appName) -DestinationPath $zipPath

Write-Host "Build complete: $outDir\$appName"
Write-Host "Portable package: $zipPath"
