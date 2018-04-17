# horlogo
[![Build Status](https://travis-ci.org/AChep/horlogo.svg?branch=master)](https://travis-ci.org/AChep/horlogo)

Simple watch face written for Android Wear 2 or higher.

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
