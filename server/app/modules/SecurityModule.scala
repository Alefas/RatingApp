package modules

import com.google.inject.AbstractModule
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.oauth.client.FacebookClient
import org.pac4j.play.{ApplicationLogoutController, CallbackController}
import org.pac4j.play.http.{DefaultHttpActionAdapter, HttpActionAdapter}
import org.pac4j.play.store.{DataStore, PlayCacheStore}
import play.api.{Configuration, Environment}

/**
  * @author Alefas
  * @since  13/11/15
  */
class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    val fbId = configuration.getString("fbId").get
    val fbSecret = configuration.getString("fbSecret").get
    val facebookClient = new FacebookClient(fbId, fbSecret)
    facebookClient.setScope(FacebookClient.DEFAULT_SCOPE + ",user_friends")
    facebookClient.setFields(FacebookClient.DEFAULT_FIELDS + ",friends")
    facebookClient.setLimit(1000)


    val clients = new Clients("http://localhost:9000/callback", facebookClient)

    val config = new Config(clients)
    bind(classOf[Config]).toInstance(config)

    bind(classOf[DataStore]).to(classOf[PlayCacheStore])

    bind(classOf[HttpActionAdapter]).to(classOf[DefaultHttpActionAdapter])

    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/")
    bind(classOf[CallbackController]).toInstance(callbackController)

    val logoutController = new ApplicationLogoutController()
    logoutController.setDefaultUrl("/")
    bind(classOf[ApplicationLogoutController]).toInstance(logoutController)
  }
}
