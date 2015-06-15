/**
 * Created by chris on 5/14/15.
 */

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import spray.json._
import spray.httpx.encoding.{Gzip, Deflate}
import spray.client.pipelining._
import HueJsonProtocol._
import scala.concurrent.Future

object HueClient {


  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  type fr = Future[HttpResponse]

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive


  val BASE_URL = "http://"+ClientConfig.hubIp+"/api/"+ClientConfig.developer



  def setLightColor(lightId: Int, color : Int):fr           = setLightColor(lightId, HueColor(hue = color))
  def turnLightOff(lightId: Int):fr                         = setLightColor(lightId, HueColor(on = false))
  def breathe(lightId: Int):fr                              = setAlert(lightId, "select")
  def longBreathe(lightId: Int):fr                          = setAlert(lightId, "lselect")
  def loopLightColor(lightId: Int):fr                       = setEffect(lightId, "colorloop")
  def solidLightColor(lightId: Int):fr                      = setEffect(lightId, "none")
  def setTemperature(lightId: Int, temp: Int):fr            = setTemperature(lightId,HueTemperature(temp))
  def setLightBrightness(lightId: Int, intensity: Int):fr   = setLightBrightness(lightId,HueBrightness(intensity))

  def listLights:fr                                              = pipeline(Get(BASE_URL+"/lights"))

  // refactor state calls
  def setLightBrightness(lightId: Int, bri: HueBrightness):fr    = pipeline(Put(BASE_URL+"/lights/"+lightId+"/state",bri))
  def setLightColor(lightId: Int, color: HueColor):fr            = pipeline(Put(BASE_URL+"/lights/"+lightId+"/state",color))
  def setEffect(lightId: Int, effect: HueEffect):fr              = pipeline(Put(BASE_URL+"/lights/"+lightId+"/state",effect))
  def setAlert(lightId: Int, alert: HueAlert):fr                 = pipeline(Put(BASE_URL+"/lights/"+lightId+"/state",alert))
  def setTemperature(lightId: Int, temp: HueTemperature)         = pipeline(Put(BASE_URL+"/lights/"+lightId+"/state",temp))
  def setLightName(lightId: Int, name: String):fr                = pipeline(Put(BASE_URL+"/lights/"+lightId, HueAttributes(name)))

  def listGroups:fr                                              = pipeline(Get(BASE_URL+"/groups"))
  def createGroup(group: HueGroup):fr                            = pipeline(Post(BASE_URL+"/groups",group))
  def getGroupInfo(groupId: Int):fr                              = pipeline(Get(BASE_URL+"/groups/"+groupId))

}
object ClientConfig {
  private val appConfig = ConfigFactory.load()
  val developer = appConfig.getConfig("clientInfo").getString("developer")
  val hubIp = appConfig.getConfig("clientInfo").getString("hub-ip")
}
