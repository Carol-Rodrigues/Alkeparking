import java.util.*

// Exercício 1: Set é uma collection que só aceita informações únicas (não repetidas). (Set: coleção com chaves,
// Array: coleção indexada). Não poderia haver dois veículos idênticos (com base na placa).

// Messages
const val SUCCESS_MESSAGE = "Welcome to AlkeParking!"
const val ERROR_MESSAGE = "Sorry, the check-in failed."
const val ERROR_CHECKOUT = "Sorry, the check-out failed."
// Numbers
const val MINUTES_IN_MILISECONDS = 60000
const val MAX_NUMBER_OF_VEHICLES = 20
const val TWO_HOURS: Long = (1000 * 60 * 60 * 2)
const val THREE_HOURS: Long = (1000 * 60 * 60 * 3)
const val MINIMUM_TIME = 120.00
const val EXCESS_TIME = 15.00
// Testing Math.ceil calculation
//const val EXCESS_TIME = 14.00
const val EXCESS_FEE = 5.00

// Function to calculate the time difference
fun getDate(time: Long): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis -= time
    }
}

fun main(args: Array<String>) {

    // Instantiating the vehicles
    println("\n** Vehicle checkin TEST **")
    val listOfVehicles = listOf<Vehicle>(
        Vehicle("AA111AA", VehicleType.CAR, getDate(THREE_HOURS), "DISCOUNT_CARD_001"),
        Vehicle("B222BBB", VehicleType.MOTOCYCLE, getDate(TWO_HOURS)),
        Vehicle("CC333CC", VehicleType.MINIBUS, getDate(TWO_HOURS)),
        Vehicle("DD444DD", VehicleType.BUS, getDate(TWO_HOURS), "DISCOUNT_CARD_002"),
        Vehicle("BB111AA", VehicleType.CAR, getDate(THREE_HOURS), "DISCOUNT_CARD_003"),
        Vehicle("CC222BBB", VehicleType.MOTOCYCLE, getDate(THREE_HOURS)),
        Vehicle("DD333CC", VehicleType.MINIBUS, getDate(THREE_HOURS)),
        Vehicle("EE444DD", VehicleType.BUS, getDate(THREE_HOURS), "DISCOUNT_CARD_004"),
        Vehicle("FF111AA", VehicleType.CAR, getDate(THREE_HOURS), "DISCOUNT_CARD_005"),
        Vehicle("GG22BBB", VehicleType.MOTOCYCLE, getDate(TWO_HOURS)),
        Vehicle("HH333CC", VehicleType.MINIBUS, getDate(TWO_HOURS)),
        Vehicle("II444DD", VehicleType.BUS, getDate(TWO_HOURS), "DISCOUNT_CARD_006"),
        Vehicle("JJ111AA", VehicleType.CAR, getDate(THREE_HOURS), "DISCOUNT_CARD_010"),
        Vehicle("KK22BBB", VehicleType.MOTOCYCLE, getDate(THREE_HOURS)),
        Vehicle("LL333CC", VehicleType.MINIBUS, getDate(THREE_HOURS)),
        Vehicle("MM444DD", VehicleType.BUS, getDate(THREE_HOURS), "DISCOUNT_CARD_011"),
        Vehicle("NN111AA", VehicleType.CAR, getDate(THREE_HOURS), "DISCOUNT_CARD_012"),
        Vehicle("OO22BBB", VehicleType.MOTOCYCLE, getDate(TWO_HOURS)),
        Vehicle("PP333CC", VehicleType.MINIBUS, getDate(TWO_HOURS)),
        Vehicle("OO22BBB", VehicleType.MOTOCYCLE, getDate(TWO_HOURS)),
        Vehicle("QQ22BBB", VehicleType.MOTOCYCLE, getDate(TWO_HOURS)),
        Vehicle("RR22BBB", VehicleType.MOTOCYCLE, getDate(THREE_HOURS))
    )

    // Instantiating Parking
    val parking = Parking(mutableSetOf())

    // Inserting each vehicle in Parking Set
    for (i in listOfVehicles.indices) {
       print("${i + 1}: ")
        parking.addVehicle(listOfVehicles[i])
    }

    /* Test -- checkout */
    println("\n** Vehicle checkout TEST **")
    // Existing plate
    parking.checkOutVehicle(listOfVehicles[0])
    parking.checkOutVehicle(listOfVehicles[2])
    // Wrong plate
    parking.checkOutVehicle(listOfVehicles[21])

    /* Test -- balance */
    println("\n** Balance TEST **")
    parking.printBalance()

    /* Test -- removing */
    println("\n** Remove vehicle TEST **")
    // List size before removal
    println("Number of vehicles: " + parking.vehicles.size)
    // Removing existing vehicle
    parking.removeVehicle("PP333CC")
    // List size after removal
    println("Number of vehicles: " + parking.vehicles.size)
    // Removing wrong vehicle
    parking.removeVehicle("XXXXXXX")
    // List size after removal (no changes)
    println("Number of vehicles: " + parking.vehicles.size)

    /* Test -- removing */
    println("\n** Printing TEST **")
    parking.listVehicles()

}

data class ParkingSpace(var vehicle: Vehicle) {

    // Não incluimos as funções de Checkout e de CalculateFee no ParkingSpace, pois não conseguimos chamar
    // a herança ou instanciar a classe Parking. Como era necessário fazer um find na listagem de placas,
    // nossa solução foi criar todas as funções na Parking.

    // A solução que pensamos para sanar o problema da instância, seria não colocar a condição de erro caso não
    // encontrassemos a placa do veículo a ser retirado via Checkout

}

open class Parking(val vehicles: MutableSet<Vehicle>) {

    var parkingBalance: Pair<Int, Int> = Pair(0,0)
    var totalOfVehicles = 0
    var totalOfWinnings = 0

    // Function to add vehicles
    fun addVehicle(vehicle: Vehicle): Boolean {
        // Unable to add (repeated plate) or max quantity of vehicles reached
        return if (vehicles.size >= MAX_NUMBER_OF_VEHICLES || !vehicles.add(vehicle)) {
            println(ERROR_MESSAGE)
            false
        }
        // In case of success
        else {
            vehicles.add(vehicle)
            println(SUCCESS_MESSAGE)
            true
        }
    }

    // Function to remove vehicles
    fun removeVehicle(plate: String): Vehicle? {
        // Search the vehicle to be removed
        val vehicleToBeRemoved = vehicles.firstOrNull { it.plate == plate }
        // Remove a vehicle on Checkout
        vehicles.remove(vehicleToBeRemoved)
        // Adding a vehicle to balance info
        totalOfVehicles++
        return vehicleToBeRemoved
    }

    // Checkout function
    fun checkOutVehicle(vehicle: Vehicle) {
        // Search a plate in vehicles list -- in case of error, print a message
        if (vehicles.find { it.plate == vehicle.plate } == null) onError()
        // In case of success, calculate fee
        else onSuccess(vehicle, calculateFee(vehicle))
    }

    // Error on checkout function
    private fun onError() {
        println(ERROR_CHECKOUT)
    }

    // Success on checkout function
    private fun onSuccess(vehicle: Vehicle, feeCalculated: Int) {
        // remove the found vehicle
        removeVehicle(vehicle.plate)
        println("Your fee is $$feeCalculated. Come back soon.")
    }

    // Calculating fee
    private fun calculateFee(vehicle: Vehicle): Int {
        // Variable is initialized to the default value of initial 2 hours
        var feeCalculated: Int = vehicle.type.fee
        // Calculated fee to be returned
        var totalFee = 0
        // Time calculation
        val timeParked = vehicle.getParkedTime()
        // 15% of discount
        val discountFee = 0.85
        // Condicional to calculate fee
        if (timeParked > MINIMUM_TIME) {
            feeCalculated += (Math.ceil(((timeParked - MINIMUM_TIME) / EXCESS_TIME)) * EXCESS_FEE).toInt()
        }
        // Discount calculation
        if (vehicle.discountCard != null) {
            totalFee = (feeCalculated * discountFee).toInt()
            totalOfWinnings += totalFee
            return totalFee
        } else {
            totalFee = feeCalculated
            totalOfWinnings += totalFee
            return totalFee
        }
    }

    // Function to print balance information
    fun printBalance() {
        parkingBalance = Pair(totalOfVehicles, totalOfWinnings)
        println("${parkingBalance.first} vehicle(s) have checked out. You have earning $${parkingBalance.second}.")
    }

    // Function to print vehicles plate
    fun listVehicles() {
        vehicles.map { print(it.plate + " ") }
    }
}

data class Vehicle(val plate: String, val type: VehicleType, val checkInTime: Calendar, val discountCard: String? = null) {

    fun getParkedTime(): Long {
        return (Calendar.getInstance().timeInMillis - checkInTime.timeInMillis) / MINUTES_IN_MILISECONDS
    }

    // Function states that 2 vehicles are equal if their plates are equal
    override fun equals(other: Any?) : Boolean {
        if (other is Vehicle) {
            return this.plate == other.plate
        }
        return super.equals(other)
    }

    // Function states that the hashCode(used internally in search functions in sets and arrays) is the hashCode of the plate
    override fun hashCode() : Int = this.plate.hashCode()
}

enum class VehicleType(val fee: Int) {
    CAR(20), MOTOCYCLE(15), MINIBUS(25), BUS(30)
}