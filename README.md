MusesServer
===========

Sources for the server part of the [Muses project](http://musesproject.eu).

##License

This package is released under the Affero GPL license, although you can use parts of it under a different license. Read [LICENSE](LICENSE) for details.

## Documentation

There is a [separate repo](https://github.com/MusesProject/Muses-Developer-Guide) for the developer guide. You can download a PDF from the [releases section](https://github.com/MusesProject/Muses-Developer-Guide/releases) or build your own.

## Build

You will have to build [Muses common](https://github.com/MusesProject/MusesCommon) module first. 

## Additional Maven dependencies

In order to satisfy weka Maven dependency you will have to download this jar: (http://users.aber.ac.uk/rkj/book/wekafull.jar), add "-1.0" at the end of the name (at the end it should be wekafull-1.0.jar), store it in /lib folder inside the MusesServer project, and then run the following command inside the project folder:

```
> mvn install:install-file -Dfile=<your_path_to_MusesServer>/lib/wekafull-1.0.jar -DgroupId=weka -DartifactId=wekafull -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=/MusesServer/lib/
```
