Tag 1
* vorab Workshop Tag (Ktor Basics, KoTest am Ende)

Tag 2
* Klassen, Objekte und Data-Klassen vorstellen
    * Wiederholung Rest-APis mit Ktor
    * zusätzlich noch Ktor Authentication
    * Übung 1: Entity, Repository, Controller für einen Teil der Anwendung
         * User, Friendship, FriendsController, MeController(get, friends)
         * Tests schreiben oder per Postman ausprobieren
         * OpenApi vorgeben
         * Diskussion Lösungen und Verbesserungen

* Exposed Verwendung
    * Infix Funktionen 
    * Übung 2: Entity auf Exposed umstellen mit DSL
    * Delegation vorstellen
    * Übung 3: Restliche Entitäten/Repositories anlegen mit DAO
         * keine weiteren Controller        
            
* Koin mit Ktor / Koin Interna
    * Reified, Destructuring
    * Übung 3: Umstellen auf Koin und restliche Controller implementieren
    * Diskussion Lösungen und Verbesserungen

* Typsicher DSL
    * DSLMarker
    * Beispiel SVG und JSON
        * Innere Extension Function
    * Übung 4: DSL für Testdaten Erzeugung für Lunch-Applikation
        
* Abschluss: Generics, Unit-Nothing    
       
Tag 3    
* Weiteres mit Ktor
    * Sessions

* Standard Lambda Scope Function vorstellen
    * Übung 4: Code nach Optimierungen durchsuchen und vorstellen

* Koroutinen Grundlagen vorstellen
    * Übung 5: Server Sent Events für Invitations mit Koroutinen und Channels
    
* Koroutinen Flow und Websockets
    * Übung 6: Chatten zwischen den Usern erlauben. Broadcast pro Lunch.    
        
* Spring und Kotlin
    * Spring Data und RestController
    * Übung 1 wiederholen, aber mit Spring
    * Diskussion Lösungen und Verbesserungen
    * R2dbc und Webflux vorstellen
    
* Type-Aliase / Inline Classes
* Sealed Classes und ADT
* Multiplatform Projekte zeigen
