$testFiles = Get-ChildItem -Path "src/main/java/com/hotelrental/logingpage" -Recurse -Filter "*Test.java"
foreach ($file in $testFiles) {
    $targetPath = $file.FullName -replace "src\\main", "src\\test"
    $targetDir = Split-Path -Parent $targetPath
    if (!(Test-Path $targetDir)) {
        New-Item -ItemType Directory -Force -Path $targetDir
    }
    Copy-Item -Path $file.FullName -Destination $targetPath -Force
    Remove-Item -Path $file.FullName -Force
}
