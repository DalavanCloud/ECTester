# ECTester
[![Build status](https://api.travis-ci.org/crocs-muni/ECTester.svg?branch=master)](https://travis-ci.org/crocs-muni/ECTester)  [![GitHub release](https://img.shields.io/github/release/crocs-muni/ECTEster.svg)](https://github.com/crocs-muni/ECTester/releases)  [![license](https://img.shields.io/github/license/crocs-muni/ECTester.svg)](https://github.com/crocs-muni/ECTester/blob/master/LICENSE)

Tests support and behavior of elliptic curve cryptography implementations on JavaCards (`TYPE_EC_FP` and `TYPE_EC_F2M`) and on selected software libraries.

## Build

ECTester uses ant. There are three parts of ECTester, the JavaCard applet used for testing, the reader app which controls it and the standalone app which tests software libraries.
```bash
ant -f build-reader.xml package               # To build the reader tool (jar) -> "dist/ECTesterReader.jar"
ant -f build-standalone.xml package           # To build the standalone tool (jar) -> "dist/ECTesterStandalone.jar"
ant -f build-applet.xml build                 # To build the applet (cap) -> "!uploader/ectester.cap".
```
Build produces both a lightweight version of the JARs and a full version of the JARs with dependencies included, the latter has the `*-dist.jar` suffix.
The standalone build tries building test binaries for all the supported libraries, and silently fails if the library is not properly supported.

## JavaCard testing

1. Upload `!uploader/ectester.cap` using your favorite tool (e.g., [GlobalPlatformPro tool](https://github.com/martinpaljak/GlobalPlatform))
2. Run `java -jar dist/ECTesterReader.jar -t -a`
3. Inspect output log with annotated results

Following operations are tested:
- Allocation of new KeyPair class for specified parameters
- Generation of KeyPair with default curve
- Setting of custom curve and KeyPair generation
- Generation of shared secret via ECDH
- Signature via ECDSA
- Behavior of card when invalid curves/points are provided (should fail)

See `java -jar ECTesterReader.jar -h` for more.

### Options

```
 -dsa,--ecdsa <count>              Sign data with ECDSA, [count] times.
 -t,--test <test_suite>            Test ECC support. [test_suite]:
                                   - default:
                                   - invalid:
                                   - wrong:
                                   - composite:
                                   - test-vectors:
 -dh,--ecdh <count>                Do EC KeyAgreement (ECDH...), [count]
                                   times.
 -e,--export                       Export the defaut curve parameters of
                                   the card(if any).
 -V,--version                      Print version info.
 -ln,--list-named <what>           Print the list of supported named
                                   curves and keys.
 -h,--help                         Print help.
 
 -a,--all                          Test all curve sizes.
 -b,--bit-size <bits>              Set curve size.
 
 -fp,--prime-field                 Use a prime field.
 -f2m,--binary-field               Use a binary field.
 
 -c,--curve <curve_file>           Use curve from file <curve_file>
                                   (field,a,b,gx,gy,r,k).
 -nc,--named-curve <cat/id>        Use a named curve, from CurveDB:
                                   <cat/id>
 -u,--custom                       Use a custom curve (applet-side
                                   embedded, SECG curves).
 -npub,--named-public <cat/id>     Use public key from KeyDB: <cat/id>
 -pub,--public <pubkey_file>       Use public key from file <pubkey_file>
                                   (wx,wy).
 -priv,--private <privkey_file>    Use private key from file
                                   <privkey_file> (s).
 -npriv,--named-private <cat/id>   Use private key from KeyDB: <cat/id>
 -k,--key <key_file>               Use keyPair from file <key_file>
                                   (wx,wy,s).
 -nk,--named-key <cat/id>          Use keyPair from KeyDB: <cat/id>

 -i,--input <input_file>           Input from file <input_file>, for ECDSA
                                   signing.
 -o,--output <output_file>         Output into file <output_file>.
 -l,--log <log_file>               Log output into file [log_file].
 -v,--verbose                      Turn on verbose logging.
    --format <format>              Output format to use. One of:
                                   text,yml,xml.
 -f,--fresh                        Generate fresh keys (set domain
                                   parameters before every generation).
 -s,--simulate                     Simulate a card with jcardsim instead
                                   of using a terminal.
 -y,--yes                          Accept all warnings and prompts.
 
 -ka,--ka-type <type>              Set KeyAgreement object [type],
                                   corresponds to JC.KeyAgreement
                                   constants.
 -sig,--sig-type <type>            Set Signature object [type],
                                   corresponds to JC.Signature constants.
```

### Actions

#### Export
`-e / --export`

Exports the default curves (if any) that are preset on the card.
Use with `-o / --output [out_file]` to output the curve parameters to a file.
For format of this file see [FORMAT](docs/FORMAT.md).

#### Test
`-t / --test [test_suite]`

Perform support and performance tests of ECC.

For more info about the test suites see [TESTS](docs/TESTS.md).

#### Generate
`-g / --generate [amount]`

Generates batches of EC keypairs and exports them.
Use with `-o / --output [out_file]` to output the generated keys to a file.

#### ECDH
`-dh / --ecdh [count]`

Performs ECDH.
Use with `-o / --output [out_file]` to output into a file.
Respects the KeyAgreement type specified in `-ka / --ka-type [type]`.

#### ECDSA
`-dsa / --ecdsa [count]`

Performs ECDSA.
Useful with `-i / --input [in_file]` to sign the contents of a file.
Use with `-o / --output [out_file]` to output into a file.
Respects the Signature type specified in `-sig / --sig-type [type]`.

#### List named curves
`-ln / --list-named []`

Lists categories of curves, keys and keypairs embedded in ECTester's jar, along with some information about them.
These can be used as arguments to the `-n[c|k|pub|priv] / --named-[curve|key|public|private]` parameters.

With the format: `category/name`.

For example:
`secg/secp192r1` identifies the SECG 192 bit prime field curve known as `secp192r1`.

For more info about the curves see [CURVES](docs/CURVES.md).

### Example


    ### Test for support and with valid and invalid EC curves
    EC type:                                             ALG_EC_FP
    EC key length (bits):                                256 bits
       KeyPair object allocation:                           OK	(0x9000)
       Generate key with def curve (fails if no def):       OK	(0x9000)
       Set valid custom curve:                              OK	(0x9000)
       Generate key with valid curve:                       OK	(0x9000)
    !! ECDH agreement with valid point:                     fail	(unknown,	0x6f00)
       ECDH agreement with invalid point (fail is good):    fail	(ILLEGAL_VALUE,	0x   1)
       ECDSA signature on random data:                      OK	(0x9000)
       Set anomalous custom curve (may fail):               OK	(0x9000)
       Generate key with anomalous curve (may fail):        fail	(unknown,	0x6f00)
       ECDH agreement with small order point (fail is good):fail	(skipped,	0x ee1)
       Set invalid custom curve (may fail):                 OK	(0x9000)
       Generate key with invalid curve (fail is good):      fail	(unknown,	0x6f00)
       Set invalid field (may fail):                        OK	(0x9000)
       Generate key with invalid field (fail si good):      fail	(unknown,	0x6f00)
   
*Explanation: ALG_EC_FP with 256b curve was tested. Is supported by card (KeyPair object allocation: OK), don't have preset default curve (Generate key with def curve: fail), custom curve can be set (Set valid custom curve: OK), new keypair can be generated (Generate key with valid curve: OK), ECDH key agreement failed to execute (ECDH agreement with valid point: fail) although it was supposed to succeed (log line is therefore marked with !!), ECDH wil fail (expected behavior) if invalid point is provided (ECDH agreement with invalid point: fail), ECDSA signature worked and verified correctly (ECDSA signature on random data: OK), anomalous curve can be set (Set anomalous custom curve: OK), however generating a key on it will fail (Generate key with anomalous curve: fail), ECDH with small-order public key provided will fail as intended (ECDH agreement with small order point: fail), invalid custom curve could be set (Set invalid custom curve: OK), new keypair cannot be generated with invalid curve (Generate key with invalid curve: fail), invalid field (non-prime) could be set (Set invalid field: OK), however a key could not be generated (Generate key with invalid field: fail).*

If you are interested in testing support for other JavaCard algorithms, please visit JCAlgTester project: https://github.com/crocs-muni/JCAlgTest


## Standalone library testing

Currently supported libraries include:
 - BouncyCastle
 - SunEC
 - libtomcrypt
 - botan
 

```
usage: ECTesterStandalone.jar [-V] [-h] [ (ecdh [-t <type>] [-n <amount>] [-b <n>] [-nc <cat/id>]) |
(ecdsa [-t <type>] [-n <amount>] [-b <n>] [-nc <cat/id>] [-f <file>]) |
(export [-t <type>] [-b <n>]) | (generate [-nc <cat/id>] [-n <amount>] [-t
<type>] [-b <n>]) | (list-data  [what]) | (list-libs) | (test [-gt <type>]
[-kt <type>] [-st <type>] [-b <n>] [-nc <cat/id>]) ] [lib]

  -V,--version   Print version info.
  -h,--help      Print help.
  [lib]   What library to use.

 ecdh:
   -t,--type <type>             Set KeyAgreement object [type].
   -n,--amount <amount>         Do ECDH [amount] times.
   -b,--bits <n>                What size of curve to use.
   -nc,--named-curve <cat/id>   Use a named curve, from CurveDB: <cat/id>

 ecdsa:
   -t,--type <type>             Set Signature object [type].
   -n,--amount <amount>         Do ECDSA [amount] times.
   -b,--bits <n>                What size of curve to use.
   -nc,--named-curve <cat/id>   Use a named curve, from CurveDB: <cat/id>
   -f,--file <file>             Input [file] to sign.

 export:
   -t,--type <type>   Set KeyPair object [type].
   -b,--bits <n>      What size of curve to use.

 generate:
   -nc,--named-curve <cat/id>   Use a named curve, from CurveDB: <cat/id>
   -n,--amount <amount>         Generate [amount] of EC keys.
   -t,--type <type>             Set KeyPairGenerator object [type].
   -b,--bits <n>                What size of curve to use.

 list-data:
   [what]   what to list.

 list-libs:

 test:
   -gt,--kpg-type <type>        Set the KeyPairGenerator object [type].
   -kt,--ka-type <type>         Set the KeyAgreement object [type].
   -st,--sig-type <type>        Set the Signature object [type].
   -b,--bits <n>                What size of curve to use.
   -nc,--named-curve <cat/id>   Use a named curve, from CurveDB: <cat/id>

```