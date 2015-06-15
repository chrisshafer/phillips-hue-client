import spray.json.DefaultJsonProtocol

/**
 * Created by chris on 5/27/15.
 */
object HueJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat      = jsonFormat5(HueColor)
  implicit val effectFormat     = jsonFormat1(HueEffect)
  implicit val groupFormat      = jsonFormat2(HueGroup)
  implicit val attributeFormat  = jsonFormat1(HueAttributes)
  implicit val brightnessFormat = jsonFormat1(HueBrightness)
  implicit val saturationFormat = jsonFormat1(HueSaturation)
  implicit val alertFormat      = jsonFormat1(HueAlert)
  implicit val tempFormat       = jsonFormat2(HueTemperature)

  implicit def stringToEffect(in:String) = HueEffect(in)
  implicit def stringToAlert(in:String) = HueAlert(in)
  implicit def stringTOAttribute(in: String) = HueAttributes(in)
  implicit def seqNameToGroup(in:(Seq[String],String)) = HueGroup(in._1,in._2)
  implicit def intToBrightness(in:Int) = HueBrightness(in)
  implicit def intToSaturation(in:Int) = HueSaturation(in)
}
trait HueState
case class HueEffect(effect: String = "none") extends HueState
case class HueAlert(alert: String = "none") extends HueState
case class HueAttributes(name: String)
case class HueTemperature(ct: Int, on: Boolean = true) extends HueState
case class HueColor(hue: Int = 0, bri: Int = 255, sat: Int = 255, on: Boolean = true, effect: String = "none") extends HueState
case class HueBrightness(bri: Int = 254) extends HueState
case class HueSaturation(sat: Int = 254) extends HueState
case class HueGroup(lights: Seq[String], name: String)


