package de.e2.lunch_scheduler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import de.e2.lunch_scheduler.com.jambit.controller.AUTH_NAME
import de.e2.lunch_scheduler.com.jambit.controller.friendsController
import de.e2.lunch_scheduler.com.jambit.controller.meController
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtService
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.User
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules
import java.io.File

fun main() {
    val server = embeddedServer(factory = Netty, port = 8080) {

        modules(friendshipModule)

        install(ContentNegotiation) {
            jackson()
        }

        install(Authentication) {
            basic(name = AUTH_NAME) {
                realm = "Ktor Server"
                validate { credentials ->
                    if (credentials.name == credentials.password) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }
        val rtService: RtService by inject()
        init(rtService)
        friendsController()
        meController()
    }
    server.start(wait = true)
}

fun init(rtService: RtService) {
    val mapper = jacksonObjectMapper()
    mapper.registerKotlinModule()

    val jsonString: String = File("./lunch-scheduler/src/main/kotlin/com.jambit.model/Users.json").readText(Charsets.UTF_8)
    val jsonTextList: List<UsersToImport> = mapper.readValue(jsonString)
    for (user in jsonTextList) {

        rtService.saveUser(User(user.Name))
    }

//    val findFirstUserByName = RtService.findFirstUserByName("Eric Fiore")
//    if(findFirstUserByName != null) {
//        print(findFirstUserByName.name);
//    }
}

data class UsersToImport(val Name: String, val PhotoUrl: String, val AuthorizationTokenExpiration: String, val AuthenticationProviderKind: Int, val AuthenticationProviderId: String)

val friendshipModule = module {
    single { RtService() }
}
