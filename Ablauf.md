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
    * Lambda With Receiver
    * Infix / Operator Funktionen 
    * Übung 2: Repository/Entity auf Exposed umstellen mit DSL
    * Delegation 
    * Exposed DAO
    * Übung 3: Restliche Entitäten/Repositories anlegen mit DAO
         * keine weiteren Controller        
            
* Koin mit Ktor / Koin Interna
    * Reified
    * Übung 4: Umstellen auf Koin und restliche Controller implementieren
    * Diskussion Lösungen und Verbesserungen

* Weiteres mit Ktor
    * Sessions
    * Locations
    * Übung Optional: Mit Locations Übersichtsseite und Detailseite für Restaurants bauen
        
* Abschluss: 
    * Generics
    * Unit-Nothing    
       
Tag 3    

* Typsicher DSL
    * Explizit Return
    * DSLMarker
    * Beispiel SVG und JSON
        * Innere Extension Function JSON und StringSpec
    * Übung 5: DSL für Testdaten Erzeugung für Lunch-Applikation
        ```
        lunch {
            host {
                name = "Rene"
            }
            restaurant {
                name = "Bla"
                ...
            }
            
            date = "1.1.2020"
            state = SCHEDULED
            notes = "Geburtstag Max"
            invitations {
                friend {
                    name = "Max"
                    response = ACCEPTED
                }
                friend {
                    name = "Maxi"
                    response = NONE
                }
            }
        }
        ```

* Standard Lambda Scope Function vorstellen
    * Übung 6: Code nach Optimierungen durchsuchen und vorstellen

* Koroutinen / Channels Grundlagen vorstellen
    * Übung 7: Server Sent Events für einen User mit Koroutinen und Channels
        * Client ohne Koroutinen
    
* Koroutinen Flow und Websockets
    * Übung 8: Chatten zwischen den Usern erlauben. Broadcast pro Lunch.    
        
* Sealed Classes und ADT am Beispiel von 
    * AuthenticationFailedCause, Websockets.Frame
        
* Spring und Kotlin
    * Spring Data und RestController
       * LateInit
    * Übung 1 wiederholen, aber mit Spring
    * R2dbc und Webflux vorstellen
    
* Type-Aliase / Inline Classes

* Multiplatform Projekte zeigen
