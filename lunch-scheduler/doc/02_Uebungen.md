# Übung 1: User und Friends

## Ziel 
* Ktor-Anwendung ohne Datenbank, um User und Friends zu verwalten

## Aufgaben
* Implementieren des `FriendsController` und teilweise des `MeController` (`get()` `friends(`)
* Http-Basic-Authentication erzwingen 
* Benötigte Repositories, Entitäten (`User`, `Friendship`) und ggf. API-Request- und API-Response-Klassen anlegen
* Daten nur im Speicher halten und beim Starten mit ein paar Test-Usern füllen (Im C#-Projekt gibt es eine JSON-Datei)
* API ausprobieren: Postman, Test-Client oder Tests 
 
# Übung 2: User und Friends persistieren 

## Ziel 
* User und Friends werden in H2-Datenbank persistiert

## Aufgaben
* Exposed-DSL benutzen, um Tabellen (`User`, `Friendship`) zu beschreiben
* Implementieren der Repositories mit Exposed-DSL
* Beim Starten der Anwendung ein paar Test-User anlegen
* API ausprobieren: siehe oben

# Übung 3: Alle Entitäten persistieren 

## Ziel 
* Alle Entitäten können in einer H2-Datenbank persistiert werden 

## Aufgaben
* Exposed-DSL benutzen, um restliche Tabellen zu beschreiben
* Exposed-DAO benutzen, um Lunch, Restaurant und Invitation zu persistieren
* Implementieren der fehlenden Repositories 
* Beim Starten der Anwendung ein paar Test-Restaurants anlegen (Im C#-Projekt gibt es eine JSON-Datei)
* Repositories testen
 
# Übung 4: Dependency-Injection mit Koin 

## Ziel 
* Benutzen von Koin für DB-Connections, Repositories und injizieren in Controller

## Aufgaben
* Koin benutzen, um Abhängigkeiten zwischen Repositories und DB Connection und zwischen Controllern und Repository aufzulösen
* Alle fehlenden Controller (RestaurantsController, InvitationController, LunchController, restlicher MeControler)) umsetzen 
* API ausprobieren: Postman oder Test-Client oder Tests 


# Optional Übung 4a: Restaurant-Pages erstellen 

## Ziel 
* Locations-API benutzen, um Restaurant-Liste und Restaurant-Details anzuzeigen  

## Aufgaben
* Neues Ktor-Modul mit Locations-API für '/restaurants' und '/restaurant/{id}' die einfache HTML-Seiten anzeigt
* Seiten im Browser aufrufen und testen

 
# Übung 5: DSL für Test-Szenarien definieren 

## Ziel 
* Eigene DSL implementieren, um Lunches für Tests anzulegen

## DSL-Idee
* Nur als Idee zu verstehen, kann auch ganz anders aussehen
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

## Aufgaben
* Klassen und Funktionen für DSL umsetzen (im ersten Schritt ohne Persistierung)   
* Persistierung der Daten in Datenbank und Integration in Tests
* Beispiel-Test

# Übung 6: Kotlin-Idiome suchen und anwenden 

## Ziel 
* Code nach Verbesserungen durchsuchen, umsetzen, diskutieren

## Aufgaben
* Wo macht der Einsatz von Standard-Scope-Funktionen Sinn?
* Welche [Idiome](https://kotlinlang.org/docs/reference/idioms.html) können noch eingesetzt werden? 
* Überarbeitung des Codes: Stellen bitte markieren und merken für anschliessende Diskussion
 
# Übung 7: Koroutinen - Events für einen User mittels SSE verschicken 

## Ziel 
* Ein User bekommt mittels SSE Infos über alles was Ihn betrifft.

## Aufgaben
* Erzeuge im `MeController` einen Endpunkt `events`. Für den aktuellen User sollen mit SSE alle interessanten Events gesendet werden.
* Nutze einen Broadcast-Channel, um aus den Repositories die Events zu verschicken und an den Client weiterzuleiten
* Öffne einen Browser auf der Event-URL und teste, ob die Events ankommen  

# Übung 8: Chat zwischen Lunch-Teilnehmer mittels Websockets 

## Ziel 
* User können sich per Websocket verbinden und für einen Lunch eine Nachricht versenden und empfangen

## Aufgaben
* Erzeuge im `MeController` einen Websocket-Endpunkt `chat`. 
* Erlaube Nachrichten in der Form: "{lunchId}:{inhalt}" zu verschicken.
* Alle anderen angemeldeten User die an dem Lunch beteiligt sind bekommen die Nachricht.
* Schreibe einen Test-Client und teste den Chat
  
# Übung 9: Spring/Jpa für die App 

## Ziel 
* Übung 1: Anstelle von Ktor mit Spring und JPA umsetzen

## Aufgaben
* siehe oben
 
