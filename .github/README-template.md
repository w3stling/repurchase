Repurchase
==================

[![Build](https://github.com/w3stling/repurchase/actions/workflows/build.yml/badge.svg)](https://github.com/w3stling/repurchase/actions/workflows/build.yml)
[![Download](https://img.shields.io/badge/download-%%version%%-brightgreen.svg)](https://search.maven.org/artifact/com.apptasticsoftware/repurchase/%%version%%/jar)
[![Javadoc](https://img.shields.io/badge/javadoc-%%version%%-blue.svg)](https://w3stling.github.io/repurchase/javadoc/%%version%%)
[![License](http://img.shields.io/:license-MIT-blue.svg?style=flat-round)](http://apptastic-software.mit-license.org)   
[![CodeQL](https://github.com/w3stling/repurchase/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/w3stling/repurchase/actions/workflows/codeql-analysis.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.apptasticsoftware%3Arepurchase&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.apptasticsoftware%3Arepurchase)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.apptasticsoftware%3Arepurchase&metric=coverage)](https://sonarcloud.io/component_measures?id=com.apptasticsoftware%3Arepurchase&metric=Coverage)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.apptasticsoftware%3Arepurchase&metric=bugs)](https://sonarcloud.io/component_measures?id=com.apptasticsoftware%3Arepurchase&metric=bugs)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.apptasticsoftware%3Arepurchase&metric=vulnerabilities)](https://sonarcloud.io/component_measures?id=com.apptasticsoftware%3Arepurchase&metric=vulnerabilities)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.apptasticsoftware%3Arepurchase&metric=code_smells)](https://sonarcloud.io/component_measures?id=com.apptasticsoftware%3Arepurchase&metric=code_smells)

Corporate action stock repurchase transactions from [Nasdaq OMX Nordic][1]

This Java library makes it easy to extract information about repurchases of own shares from Nasdaq OMX Nordic.

Examples
--------
Get repurchase transactions
```java
Repurchase repurchase = new Repurchase();
List<Transaction> transactions = Repurchase.getTransactions()
                                           .collect(Collectors.toList());
```


Download
--------

Download [the latest JAR][2] or grab via [Maven][3] or [Gradle][4].

### Maven setup
Add dependency declaration:
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.apptasticsoftware</groupId>
            <artifactId>repurchase</artifactId>
            <version>%%version%%</version>
        </dependency>
    </dependencies>
    ...
</project>
```

### Gradle setup
Add dependency declaration:
```groovy
dependencies {
    implementation 'com.apptasticsoftware:repurchase:%%version%%'
}
```

Repurchase library requires at minimum Java 11.

License
-------

    MIT License
    
    Copyright (c) %%year%%, Apptastic Software
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.


[1]: http://www.nasdaqomx.com
[2]: https://search.maven.org/artifact/com.apptasticsoftware/repurchase/%%version%%/jar
[3]: https://maven.apache.org
[4]: https://gradle.org