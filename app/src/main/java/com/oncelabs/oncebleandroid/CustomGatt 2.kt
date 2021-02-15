package com.teknikio.tekniverse.model

import android.content.Context
import android.util.Log
import com.oncelabs.onceble.core.peripheral.OBPeripheral
import com.oncelabs.onceble.core.peripheral.gattClient.OBCharacteristic
import com.oncelabs.onceble.core.peripheral.gattClient.OBGatt
import com.oncelabs.onceble.core.peripheral.gattClient.OBService
import java.util.*

///*  Battery service UUIDs */
val BLUEBIRD_BATTERY_SERVICE_UUID = UUID.fromString("180F-0000-1000-8000-00805F9B34FB")//UUID.fromString( "180F")
val BLUEBIRD_BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("2A19-0000-1000-8000-00805F9B34FB")

/*  User interface service UUIDs */
//val BLUEBIRD_USER_INTERFACE_SERVICE_UUID           = UUID.fromString( "EF680300-9B35-4933-9B10-52FFA9740042")
//val BLUEBIRD_LED_CHARACTERISTIC_UUID               = UUID.fromString( "EF680301-9B35-4933-9B10-52FFA9740042")
//val BLUEBIRD_BUTTON_CHARACTERISTIC_UUID            = UUID.fromString( "EF680302-9B35-4933-9B10-52FFA9740042")

/*ENVIRONMENTAL SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_ENVIRONMENTAL: UUID = UUID.fromString("00001000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_TEMPERATURE =
    UUID.fromString("00001001-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_HUMIDITY = UUID.fromString("00001002-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_AMBIENT_LIGHT =
    UUID.fromString("00001003-C356-78AB-3C46-339399E84975")

/*MOTION SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_MOTION = UUID.fromString("00002000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_ACCELERATION =
    UUID.fromString("00002001-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_GYROSCOPE = UUID.fromString("00002002-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_QUATERNION =
    UUID.fromString("00002003-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_PEDOMETER = UUID.fromString("00002004-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_TAP = UUID.fromString("00002005-C356-78AB-3C46-339399E84975")

/*GPIO SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_GPIO = UUID.fromString("00003000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_GPIO1 = UUID.fromString("00003001-C356-78AB-3C46-339399E84975")//
val BLUEBIRD_UUID_CHARACTERISTIC_GPIO2 = UUID.fromString("00003002-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_GPIO3 = UUID.fromString("00003004-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_GPIO4 = UUID.fromString("00003005-C356-78AB-3C46-339399E84975")

val BLUEBIRD_UUID_CHARACTERISTIC_CAP_TOUCH_BUTTON =
    UUID.fromString("00003003-C356-78AB-3C46-339399E84975")

/*LED SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_LED = UUID.fromString("00004000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_LED_COLOR = UUID.fromString("00004001-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_LED_MODE = UUID.fromString("00004002-C356-78AB-3C46-339399E84975")

/*SERIAL SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_SERIAL = UUID.fromString("00005000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_TX_RX = UUID.fromString("00005001-C356-78AB-3C46-339399E84975")

/*AUDIO SERVICE UUIDS*/
val BLUEBIRD_UUID_SERVICE_AUDIO = UUID.fromString("00006000-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_PRELOADED = UUID.fromString("00006001-C356-78AB-3C46-339399E84975")
val BLUEBIRD_UUID_CHARACTERISTIC_STREAMING = UUID.fromString("00006002-C356-78AB-3C46-339399E84975")

/*DEVICE INFORMATION SERVICE UUIDS */
val BLUEBIRD_UUID_DEVICE_INFORMATION_SERVICE = UUID.fromString("180A-0000-1000-8000-00805F9B34FB")
val BLUEBIRD_UUID_CHARACTERISTIC_FIRMWARE_REVISION = UUID.fromString("2A26-0000-1000-8000-00805F9B34FB")
val BLUEBIRD_UUID_CHARACTERISTIC_HARDWARE_REVISION = UUID.fromString("2A27-0000-1000-8000-00805F9B34FB")
val BLUEBIRD_UUID_CHARACTERISTIC_SOFTWARE_REVISION = UUID.fromString("2A28-0000-1000-8000-00805F9B34FB")

class BluebirdGatt() : OBGatt() {
    var bluebirdEnvironmentalService: OBService? = null
    var bluebirdTemperatureCharacteristic: OBCharacteristic? = null
    var bluebirdHumidityCharacteristic: OBCharacteristic? = null
    var bluebirdAmbientLightCharacteristic: OBCharacteristic? = null

    var bluebirdMotionService: OBService? = null
    var bluebirdAccelerationCharacteristic: OBCharacteristic? = null
    var bluebirdGyroscopeCharacteristic: OBCharacteristic? = null
    var bluebirdQuaternionCharacteristic: OBCharacteristic? = null
    var bluebirdPedometerCharacteristic: OBCharacteristic? = null
    var bluebirdTapCharacteristic: OBCharacteristic? = null

    var bluebirdGpioService: OBService? = null
    var bluebirdGpioOneCharacteristic: OBCharacteristic? = null
    var bluebirdGpioTwoCharacteristic: OBCharacteristic? = null
    var bluebirdGpioThreeCharacteristic: OBCharacteristic? = null
    var bluebirdGpioFourCharacteristic: OBCharacteristic? = null
    var bluebirdCapTouchButtonCharacteristic: OBCharacteristic? = null

    var bluebirdLedService: OBService? = null
    var bluebirdLedColorCharacteristic: OBCharacteristic? = null
    var bluebirdLedModeCharacteristic: OBCharacteristic? = null

    var bluebirdSerialService: OBService? = null
    var bluebirdTxRxCharacteristic: OBCharacteristic? = null

    var bluebirdAudioService: OBService? = null
    var bluebirdPreloadedAudioCharacteristic: OBCharacteristic? = null
    var bluebirdStreamingAudioCharacteristic: OBCharacteristic? = null

    var bluebirdBatteryService: OBService? = null
    var bluebirdBatteryLevelCharacteristic: OBCharacteristic? = null

    var bluebirdDeviceInformationService: OBService? = null
    var bluebirdFirmwareRevisionCharacteristic: OBCharacteristic? = null
    var bluebirdSoftwareRevisionCharacteristic: OBCharacteristic? = null
    var bluebirdHardwareRevisionCharacteristic: OBCharacteristic? = null

    init {
        super.addServices(
            arrayOf(
                /* Environmental service */
                OBService(
                    BLUEBIRD_UUID_SERVICE_ENVIRONMENTAL,
                    { Log.d("BluebirdGatt", "Assigned Enviro Service"); this.bluebirdEnvironmentalService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_TEMPERATURE,
                            { this.bluebirdTemperatureCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_HUMIDITY,
                            { this.bluebirdHumidityCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_AMBIENT_LIGHT,
                            { this.bluebirdAmbientLightCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),
                /*  Audio Service  */
                OBService(
                    BLUEBIRD_UUID_SERVICE_AUDIO,
                    { this.bluebirdAudioService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_PRELOADED,
                            { this.bluebirdPreloadedAudioCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_STREAMING,
                            { this.bluebirdStreamingAudioCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),
                /*Device information Service*/
                OBService(
                    BLUEBIRD_UUID_DEVICE_INFORMATION_SERVICE,
                    { this.bluebirdDeviceInformationService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_FIRMWARE_REVISION,
                            { this.bluebirdFirmwareRevisionCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_SOFTWARE_REVISION,
                            { this.bluebirdSoftwareRevisionCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_HARDWARE_REVISION,
                            { this.bluebirdHardwareRevisionCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),
                /*  Battery Service  */
                OBService(
                    BLUEBIRD_BATTERY_SERVICE_UUID,
                    { this.bluebirdBatteryService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_BATTERY_LEVEL_CHARACTERISTIC_UUID,
                            { this.bluebirdBatteryLevelCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),
                /*   Motion Service  */
                OBService(
                    BLUEBIRD_UUID_SERVICE_MOTION,
                    { this.bluebirdMotionService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_ACCELERATION,
                            { this.bluebirdAccelerationCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_GYROSCOPE,
                            { this.bluebirdGyroscopeCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_QUATERNION,
                            { this.bluebirdQuaternionCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_PEDOMETER,
                            { this.bluebirdPedometerCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_TAP,
                            { this.bluebirdTapCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),

                /*  GPIO Service  */
                OBService(
                    BLUEBIRD_UUID_SERVICE_GPIO,
                    { this.bluebirdGpioService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_GPIO1,
                            { this.bluebirdGpioOneCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_GPIO2,
                            { this.bluebirdGpioTwoCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_GPIO3,
                            { this.bluebirdGpioThreeCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_GPIO4,
                            { this.bluebirdGpioFourCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_CAP_TOUCH_BUTTON,
                            { this.bluebirdCapTouchButtonCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),

                /*  Led Service  */
                OBService(
                    BLUEBIRD_UUID_SERVICE_LED,
                    { this.bluebirdLedService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_LED_COLOR,
                            { this.bluebirdLedColorCharacteristic = it },
                            arrayOf()
                        ),
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_LED_MODE,
                            { this.bluebirdLedModeCharacteristic = it },
                            arrayOf()
                        )
                    )
                ),

                /*  Serial Service  */
                OBService(
                    BLUEBIRD_UUID_SERVICE_SERIAL,
                    { this.bluebirdSerialService = it },
                    arrayOf(
                        OBCharacteristic(
                            BLUEBIRD_UUID_CHARACTERISTIC_TX_RX,
                            { this.bluebirdTxRxCharacteristic = it },
                            arrayOf()
                        )
                    )
                )
            )
        )
    }
}