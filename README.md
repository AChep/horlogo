# horlogo
[![Build Status](https://travis-ci.org/AChep/horlogo.svg?branch=master)](https://travis-ci.org/AChep/horlogo)

<img alt="Logo" align="right" height="220"
   src="https://github.com/AChep/horlogo/raw/master/art/icon_512.png" />

Simple watch face written for Android Wear 2 or higher.

Report a bug or request a feature
----------------
Before creating a new issue please make sure that same or similar issue is not already created by checking [open issues][2] and [closed issues][3] *(please note that there might be multiple pages)*. If your issue is already there, don't create a new one, but leave a comment under already existing one.

Checklist for creating issues:

- Keep titles short but descriptive.
- For feature requests leave a clear description about the feature with examples where appropriate.
- For bug reports leave as much information as possible about your device, android version, etc.
- For bug reports also write steps to reproduce the issue.

[Create new issue][1]

Versioning
----------------
For transparency in a release cycle and in striving to maintain backward compatibility, a project should be maintained under the Semantic Versioning guidelines. Sometimes we screw up, but we should adhere to these rules whenever possible.

Releases will be numbered with the following format: `<major>.<minor>.<patch>` and constructed with the following guidelines:
- Breaking backward compatibility bumps the major while resetting minor and patch
- New additions without breaking backward compatibility bumps the minor while resetting the patch
- Bug fixes and misc changes bumps only the patch

For more information on SemVer, please visit http://semver.org/.

Build
----------------
Clone the project and come in:

``` bash
$ git clone git://github.com/AChep/horlogo.git
$ cd horlogo/
```

Repository doesn't include `wear/horlogo-release.properties`, `wear/horlogo-release.keystore` so you have to create them manually before building project.

##### wear/horlogo-release.keystore
Check out this answer ["How can I create a keystore?"](http://stackoverflow.com/a/15330139/1408535)
##### wear/horlogo-release.properties
The structure of the file:
```
key_alias=****
password_store=****
password_key=****
```
| Key | Description |
| --- | --- |
| `key_alias` | Key alias used to generate keystore |
| `password_store` | Your keystore password |
| `password_key` | Your alias key password |

[1]: https://github.com/AChep/horlogo/issues/new
[2]: https://github.com/AChep/horlogo/issues?state=open
[3]: https://github.com/AChep/horlogo/issues?state=closed
