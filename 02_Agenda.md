# Tag 1
* Einsteiger Workshop 

# Tag 2
## Projekt vorstellen, Klassen im Detail und Ktor Wiederholung
* [Lunch Scheduler](lunch-scheduler/doc/01_Project.md)
* [Klassen, Objekte, Vererbung im Detail](misc/src/test/kotlin/Klassen_und_Objekte.kt)
* Rest mit Ktor (Wiederholung)
    * [Json mit Jackson](ktor/src/main/kotlin/rest/jackson/KtorRestJacksonServer.kt)
    * [Json mit Kotlin Serialization](ktor/src/main/kotlin/rest/serialization/KtorRestSerializationClient.kt)         
* [Ktor Authentication](ktor/src/main/kotlin/auth/KtorAuthApplication.kt)
* [Übung 1: User und Friends](lunch-scheduler/doc/02_Uebungen.md)


## Lambdas With Receiver, Infix / Operatoren, Delegation und Exposed
* [Lambda With Receiver](misc/src/test/kotlin/LambdasWithReceiver.kt)
* [Infix Funktionen/ Operatoren](misc/src/test/kotlin/Infix_Operatoren.kt) 
* [Exposed DSL](https://github.com/JetBrains/Exposed)
    * [Einstieg](exposed/src/test/kotlin/dsl/ExposedDSLStart.kt)
    * [Detail](exposed/src/test/kotlin/dsl/ExposedDSLMore.kt)
* [Übung 2: User und Friends persistieren](lunch-scheduler/doc/02_Uebungen.md)
* [Delegation Properties und Interfaces](misc/src/test/kotlin/Delegation.kt)
    * [Gradle](build.gradle.kts)
* [Exposed DAO](exposed/src/test/kotlin/dao/ExposedDao.kt)
* [Übung 3: Alle Entitäten persistieren](lunch-scheduler/doc/02_Uebungen.md)

            
## Generics, Reified und Koin            
* [Generics](misc/src/test/kotlin/Generics.kt)
* [Reified](misc/src/test/kotlin/Reified.kt)
* [Dependency Injection mit Koin](https://insert-koin.io/)
    * [Dependency Injection](koin/src/test/kotlin/DependencyInjection.kt)
    * [Mocking mit KoinTest](koin/src/test/kotlin/MockingWithKoinJunitTest.kt)
    * [Koin und Ktor](koin/src/main/kotlin/KtorExample.kt)
* [Übung 4: Dependency-Injection mit Koin](lunch-scheduler/doc/02_Uebungen.md)


## Optional: Weiteres mit Ktor
* Sessions
    * [Einstieg](ktor/src/main/kotlin/session/KtorSessionApplication.kt)
    * [Eigener Serializer](ktor/src/main/kotlin/session/KtorSessionExtApplication.kt)
* [Locations - Typsichere Routen](ktor/src/main/kotlin/location/LocationServer.kt)
* [Übung 4a: Restaurant-Pages erstellen](lunch-scheduler/doc/02_Uebungen.md)
        
        
# Tag 3    

## Typsicher DSLs mit Kotlin
* [Beispiel SVG](misc/src/main/kotlin/dsl/SVGKtorMain.kt) 
* [Beispiel JSON](misc/src/main/kotlin/dsl/KSon1.kt)
    * Hinweis: Innere Extension Function (JSON und StringSpec)
* [DSLMarker](misc/src/test/kotlin/DSLMarker.kt)
* [Explizites Return](misc/src/test/kotlin/ExplizitesReturn.kt)
* [Übung 5: DSL für Test-Szenarien definieren](lunch-scheduler/doc/02_Uebungen.md)


## Standard Scope Functions und Idome 
* [Standard Scope Function](misc/src/test/kotlin/Standard_Lambda_Scope_Funktionen.kt) 
* [Idiome](https://kotlinlang.org/docs/reference/idioms.html)
* [Übung 6: Kotlin-Idiome suchen und anwenden](lunch-scheduler/doc/02_Uebungen.md)

## Koroutinen und Ktor Websockets
* [Beispiel Collage](coroutines/src/main/kotlin/collage/Collage.kt) und [Collage Async](coroutines/src/main/kotlin/collage/CollageAsync.kt) 
* [Koroutinen / Channels Grundlagen](coroutines/src/test/kotlin/BasicCoroutine.kt)
* [Übung 7: Events für einen User mittels SSE verschicken](lunch-scheduler/doc/02_Uebungen.md)
* [Cold Streams mit Flows](coroutines/src/test/kotlin/MoreCoroutine.kt)
* Websockets mit Ktor
    * [Echo](coroutines/src/main/kotlin/websocket/EchoServer.kt)
    * [Broadcast](coroutines/src/main/kotlin/websocket/EchoBroadcastServer.kt)
* [Übung 8: Chat zwischen Lunch-Teilnehmer mittels Websockets](lunch-scheduler/doc/02_Uebungen.md)

                
## Spring(Boot) und R2dbc
* [Spring und Gradle-Open-Plugin](spring/build.gradle.kts)
* [Konstruktor Injection](spring/src/main/kotlin/annotations/RestService.kt) und [Field Injection via LateInit](spring/src/main/kotlin/annotations/InitConfiguration.kt)
* [Spring Data](spring/src/main/kotlin/annotations/Persistence.kt)
* [RestController](spring/src/main/kotlin/annotations/RestService.kt)
* [Functional Bean Registration](spring/src/main/kotlin/functional/AdresseFunctionalApplication.kt)
* [Übung 8: User und Friends mit Spring](lunch-scheduler/doc/02_Uebungen.md)
* [Reactive Datenbankzugriffe mit R2dbc](r2dbc/src/test/kotlin/BasicR2DBC.kt)
* [Webflux und Koroutinen](spring-reactive/src/main/kotlin/annotations/RestService.kt) 


## Sonstiges und Ausblick          
* [Type-Aliase / Import Aliase / Inline Classes](misc/src/test/kotlin/TypeAlias_Inline_Class.kt)        
* [Sealed Classes und ADT](misc/src/test/kotlin/SealedClassesADT.kt) 
    * Weitere Beispiele: AuthenticationFailedCause, Websockets.Frame
* [Flux-Beispiel - Type-Aliase, Sealed-Classes, Generics](misc/src/test/kotlin/FluxBeispiel.kt)           
* Multiplatform Projekt
