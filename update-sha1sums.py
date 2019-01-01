#!/usr/bin/env python
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017-2018 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from hashlib import sha1
import sys

def cleanup(lines):
    for index, line in enumerate(lines):
        # Remove '\n' character
        line = line[:-1]

        # Skip empty or commented lines
        if len(line) == 0 or line[0] == '#':
            continue

        # Drop SHA1 hash, if existing
        if '|' in line:
            line = line.split('|')[0]
            lines[index] = '%s\n' % (line)

def update(lines, device, vendor, vendorPath, needSHA1=False):
    for index, line in enumerate(lines):
        # Remove '\n' character
        line = line[:-1]

        # Skip empty lines
        if len(line) == 0:
            continue

        # Check if we need to set SHA1 hash for the next files
        if line[0] == '#':
            needSHA1 = (' - from' in line)
            continue

        if needSHA1:
            # Remove existing SHA1 hash
            line = line.split('|')[0]
            filePath = line.split(':')[1] if len(line.split(':')) == 2 else line

            if filePath[0] == '-':
                file = open('%s/%s' % (vendorPath, filePath[1:]), 'rb').read()
            else:
                file = open('%s/%s' % (vendorPath, filePath), 'rb').read()

            hash = sha1(file).hexdigest()
            lines[index] = '%s|%s\n' % (line, hash)

def gensha1(lines, device, vendor, vendorPath):
    if len(sys.argv) == 2 and sys.argv[1] == '-c':
        cleanup(lines)
    else:
        update(lines, device, vendor, vendorPath, False)

def writeback(lines, filename):
    try:
        with open(filename, 'w') as file:
            for line in lines:
                file.write(line);
    except IOError as err:
        print(str(err))

if __name__ == "__main__":
    filename1 = 'proprietary-files-qc.txt'
    filename2 = 'proprietary-files.txt'
    lines1 = [ line for line in open(filename1, 'r') ]
    lines2 = [ line for line in open(filename2, 'r') ]

    device='msm8953-common'
    vendor='xiaomi'
    vendorPath = '../../../vendor/' + vendor + '/' + device + '/proprietary'
    gensha1(lines1, device, vendor, vendorPath)

    device='mido'
    vendor='xiaomi'
    vendorPath = '../../../vendor/' + vendor + '/' + device + '/proprietary'
    gensha1(lines2, device, vendor, vendorPath)

    writeback(lines1, filename1)
    writeback(lines2, filename2)
