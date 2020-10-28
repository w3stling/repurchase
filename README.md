Repurchase
==================

[![Build Status](https://travis-ci.com/w3stling/repurchase.svg?branch=master)](https://travis-ci.com/w3stling/repurchase)
[![Download](https://api.bintray.com/packages/apptastic/maven-repo/repurchase/images/download.svg)](https://bintray.com/apptastic/maven-repo/repurchase/_latestVersion)
[![Javadoc](https://img.shields.io/badge/javadoc-1.0.2-blue.svg)](https://w3stling.github.io/repurchase/javadoc/1.0.2)
[![License](http://img.shields.io/:license-MIT-blue.svg?style=flat-round)](http://apptastic-software.mit-license.org)   
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Arepurchase&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.apptastic%3Arepurchase)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Arepurchase&metric=coverage)](https://sonarcloud.io/component_measures?id=com.apptastic%3Arepurchase&metric=Coverage)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Arepurchase&metric=bugs)](https://sonarcloud.io/component_measures?id=com.apptastic%3Arepurchase&metric=bugs)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Arepurchase&metric=vulnerabilities)](https://sonarcloud.io/component_measures?id=com.apptastic%3Arepurchase&metric=vulnerabilities)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.apptastic%3Arepurchase&metric=code_smells)](https://sonarcloud.io/component_measures?id=com.apptastic%3Arepurchase&metric=code_smells)

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
Add JCenter repository for resolving artifact:
```xml
<project>
    ...
    <repositories>
        <repository>
            <id>jcenter</id>
            <url>https://jcenter.bintray.com</url>
        </repository>
    </repositories>
    ...
</project>
```

Add dependency declaration:
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.apptastic</groupId>
            <artifactId>repurchase</artifactId>
            <version>1.0.2</version>
        </dependency>
    </dependencies>
    ...
</project>
```

### Gradle setup
Add JCenter repository for resolving artifact:
```groovy
repositories {
    jcenter()
}
```

Add dependency declaration:
```groovy
dependencies {
    implementation 'com.apptastic:repurchase:1.0.2'
}
```

Repurchase library requires at minimum Java 11.

License
-------

    MIT License
    
    Copyright (c) 2020, Apptastic Software
    
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
[2]: https://bintray.com/apptastic/maven-repo/repurchase/_latestVersion
[3]: https://maven.apache.org
[4]: https://gradle.org