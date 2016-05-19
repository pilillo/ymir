# ymir

> An Android library for efficient management and query of timeseries, for the extraction of analytics

[Ymir](http://norse-mythology.org/gods-and-creatures/giants/ymir/) is the first being coming into life in the nordic tradition, being born when fire from Muspelheim and ice from Niflheim met in the abyss of Ginnungagap.
As such, he represents the origin of time and nature.

[ ![Download](https://api.bintray.com/packages/andrea/maven/ymir-ts-management/images/download.svg) ](https://bintray.com/andrea/maven/ymir-ts-management/_latestVersion)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1070260a2bb74430a977a44416a26299)](https://www.codacy.com/app/andrea-monacchi/ymir?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pilillo/ymir&amp;utm_campaign=Badge_Grade)

**Lightweight database storage**
Ymir uses the Realm.io mobile database for its persistency. Realm offers a replacement for the well known SQLite file database. In particular, it favours speed and simplicity, as well as portability. More [here](https://realm.io/).


**Efficient query of timeseries**
Efficient query of timeseries is achieved by employing a tree map, i.e., a hashmap whose keys are organized in a tree structure to preserve the ordering.
The tree map guarantees a log(n) time complexity for contains, get, put and remove operations.
In particular, the table maps a Long to a Double object, i.e. a unix timestamp to a double value, although practically different data types can be used. The importan matter is that the keys are consistent with the equals and comparable interfaces.

Further informations is available here:
* [Java TreeMap<K,V> Class](https://docs.oracle.com/javase/7/docs/api/java/util/TreeMap.html)
* [Java SortedMap<K,V> Interface](https://docs.oracle.com/javase/7/docs/api/java/util/SortedMap.html)


**Importing the library in your Android project**
Ymir is part of the jcenter maven repository, which is the default repo in all newest Android projects.
This means you can import Ymir by simply specifying the following dependencies.
```
compile 'com.github.pilillo:ymir-ts-management:0.0.1'
compile 'io.realm:realm-android:0.84.1'
```
