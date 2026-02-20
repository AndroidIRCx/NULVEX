$ErrorActionPreference = 'Stop'
$rootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$rootDir = Split-Path -Parent $rootDir
python "$rootDir/scripts/transifex/transifex.py" pull --root "$rootDir"
