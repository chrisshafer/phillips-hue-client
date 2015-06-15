/**
 * Created by chris on 5/10/15.
 */
import scala.concurrent.duration._
import akka.actor.ActorSystem
import spray.routing.{Route, SimpleRoutingApp}
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import spray.json._
import MediaTypes._
import HueJsonProtocol._
import scala.util.{Try, Failure, Success}

// TO-DO refactor endpoint paths
object HelloHue extends App with SimpleRoutingApp {
    implicit val system = ActorSystem("simple-routing-app")
    import system.dispatcher
    import HueJsonProtocol._
    startServer("localhost", port = 8081) {
      get {
          path("lights"){
            onComplete(HueClient.listLights){
              handleResponse
            }
          } ~
          path("groups"){
            onComplete(HueClient.listGroups){
              handleResponse
            }
          }
      } ~
      post {
          path("setName" / IntNumber ) { (id) =>
            entity(as[String]) { name =>
              onComplete(HueClient.setLightName(id, name)) {
                handleResponse
              }
            }
          } ~
          path("setLightColor" / IntNumber / IntNumber) { (id,color) =>
            onComplete(HueClient.setLightColor(id,color)){
              handleResponse
            }
          } ~
          path("setLightTemperature" / IntNumber / IntNumber) { (id,temperature) =>
            onComplete(HueClient.setTemperature(id,temperature)){
              handleResponse
            }
          } ~
          path("setLightBrightness" / IntNumber / IntNumber) { (id,intensity) =>
            onComplete(HueClient.setLightBrightness(id,intensity)){
              handleResponse
            }
          } ~
          path("setLightSaturation" / IntNumber / IntNumber) { (id,intensity) =>
            onComplete(HueClient.setLightColor(id,intensity)){
              handleResponse
            }
          } ~
          path("power" / IntNumber ) { (id) =>
            onComplete(HueClient.turnLightOff(id)){
              handleResponse
            }
          } ~
          path("loop" / IntNumber / IntNumber ) { (id, status) =>
            onComplete{
              status match {
                case x if x > 0 => HueClient.loopLightColor(id)
                case _ => HueClient.solidLightColor(id)
              }
            }{
              handleResponse
            }
          } ~
          path("effect" / IntNumber ) { (id) =>
            entity(as[String]) { effect =>
              onComplete(HueClient.setEffect(id, effect)) {
                handleResponse
              }
            }
          } ~
          path("alert" / IntNumber ) { (id) =>
            entity(as[String]) { alert =>
              onComplete(HueClient.setAlert(id, alert)) {
                handleResponse
              }
            }
          } ~
          path("groups"){
            entity(as[HueGroup]) { group =>
              onComplete(HueClient.createGroup(group)) {
                handleResponse
              }
            }
          }

        }
    }.onComplete {
      case Success(b) =>
        println(s"Successfully bound to ${b.localAddress}")
      case Failure(ex) =>
        println(ex.getMessage)
        system.shutdown()
    }

    def handleResponse(response : Try[HttpResponse]): Route = {
      response match {
        case Success(res) =>
          complete(res)
        case Failure(t) =>
          complete(StatusCodes.BadRequest)
      }
    }



}
