# Gradle 9 на Windows не находит тестовые классы, если путь проекта содержит кириллицу (например Java-Магазин).
# Этот скрипт копирует проект во временную папку с латинским путём и запускает тесты там.

$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot
$tempRoot = Join-Path $env:TEMP "internet-shop-gradle-test"

if (-not $env:JAVA_HOME) {
    $candidates = @(
        "$env:USERPROFILE\.jdks\temurin-25.0.3",
        "$env:USERPROFILE\.jdks\openjdk-25"
    )
    foreach ($c in $candidates) {
        if (Test-Path $c) { $env:JAVA_HOME = $c; break }
    }
}

Write-Host "Копирование проекта в: $tempRoot"
if (Test-Path $tempRoot) { Remove-Item $tempRoot -Recurse -Force }
New-Item -ItemType Directory -Path $tempRoot | Out-Null
robocopy $projectRoot $tempRoot /E /XD build .gradle .idea /NFL /NDL /NJH /NJS | Out-Null

Push-Location $tempRoot
try {
    Write-Host "Запуск тестов (JAVA_HOME=$env:JAVA_HOME)..."
    & .\gradlew.bat test
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    Write-Host ""
    Write-Host "Тесты успешно пройдены."
    Write-Host "Отчёт: $tempRoot\build\reports\tests\test\index.html"
} finally {
    Pop-Location
}
