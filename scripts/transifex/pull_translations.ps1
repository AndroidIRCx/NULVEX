$ErrorActionPreference = 'Stop'
$rootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$rootDir = Split-Path -Parent $rootDir

if (Get-Command py -ErrorAction SilentlyContinue) {
    & py -3 "$rootDir/scripts/transifex/transifex.py" pull --root "$rootDir"
} elseif (Get-Command python -ErrorAction SilentlyContinue) {
    & python "$rootDir/scripts/transifex/transifex.py" pull --root "$rootDir"
} elseif (Get-Command python3 -ErrorAction SilentlyContinue) {
    & python3 "$rootDir/scripts/transifex/transifex.py" pull --root "$rootDir"
} else {
    throw "Python interpreter not found. Install Python 3 or add 'py'/'python' to PATH."
}
