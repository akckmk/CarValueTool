package com.carvaluetool.core

import com.carvaluetool.core.Segment.COMPACT_SUV
import com.carvaluetool.core.Segment.ECONOMY
import com.carvaluetool.core.Segment.ELECTRIC
import com.carvaluetool.core.Segment.FULLSIZE
import com.carvaluetool.core.Segment.FULLSIZE_SUV
import com.carvaluetool.core.Segment.LUXURY
import com.carvaluetool.core.Segment.LUXURY_SUV
import com.carvaluetool.core.Segment.MIDSIZE
import com.carvaluetool.core.Segment.MIDSIZE_SUV
import com.carvaluetool.core.Segment.MINIVAN
import com.carvaluetool.core.Segment.PICKUP
import com.carvaluetool.core.Segment.SPORTS

/**
 * Built-in, offline catalog of common vehicles sold in the US market.
 * MSRPs are approximate mid-trim prices in current dollars; model-year ranges are approximate.
 */
object VehicleCatalog {

    const val EARLIEST_YEAR = 1990

    private fun m(name: String, segment: Segment, msrp: Int, from: Int, to: Int? = null) =
        ModelSpec(name, segment, msrp, from, to)

    val makes: List<Make> = listOf(
        Make("Acura", 1.05, listOf(
            m("Integra", ECONOMY, 34_000, 1990, 2001), m("Integra", ECONOMY, 34_000, 2023),
            m("TLX", LUXURY, 46_000, 2015), m("TL", LUXURY, 44_000, 1996, 2014),
            m("RDX", LUXURY_SUV, 47_000, 2007), m("MDX", LUXURY_SUV, 54_000, 2001),
        )),
        Make("Audi", 1.15, listOf(
            m("A3", LUXURY, 40_000, 2006), m("A4", LUXURY, 45_000, 1996),
            m("A6", LUXURY, 60_000, 1995), m("Q3", LUXURY_SUV, 42_000, 2015),
            m("Q5", LUXURY_SUV, 50_000, 2009), m("Q7", LUXURY_SUV, 64_000, 2007),
            m("e-tron / Q8 e-tron", ELECTRIC, 78_000, 2019), m("R8", SPORTS, 160_000, 2008, 2023),
        )),
        Make("BMW", 1.18, listOf(
            m("3 Series", LUXURY, 48_000, 1990), m("5 Series", LUXURY, 60_000, 1990),
            m("7 Series", LUXURY, 98_000, 1990), m("X1", LUXURY_SUV, 43_000, 2013),
            m("X3", LUXURY_SUV, 51_000, 2004), m("X5", LUXURY_SUV, 68_000, 2000),
            m("X7", LUXURY_SUV, 85_000, 2019), m("M3", SPORTS, 78_000, 1995),
            m("i4", ELECTRIC, 58_000, 2022), m("iX", ELECTRIC, 88_000, 2022),
        )),
        Make("Buick", 1.08, listOf(
            m("Encore", COMPACT_SUV, 26_000, 2013, 2022), m("Encore GX", COMPACT_SUV, 28_000, 2020),
            m("Envision", COMPACT_SUV, 37_000, 2016), m("Enclave", MIDSIZE_SUV, 47_000, 2008),
            m("LaCrosse", FULLSIZE, 36_000, 2005, 2019),
        )),
        Make("Cadillac", 1.18, listOf(
            m("CT4", LUXURY, 37_000, 2020), m("CT5", LUXURY, 44_000, 2020),
            m("CTS", LUXURY, 48_000, 2003, 2019), m("XT4", LUXURY_SUV, 39_000, 2019),
            m("XT5", LUXURY_SUV, 46_000, 2017), m("Escalade", FULLSIZE_SUV, 92_000, 1999),
            m("Lyriq", ELECTRIC, 62_000, 2023),
        )),
        Make("Chevrolet", 1.02, listOf(
            m("Malibu", MIDSIZE, 28_000, 1997, 2025), m("Impala", FULLSIZE, 32_000, 1994, 2020),
            m("Cruze", ECONOMY, 22_000, 2011, 2019), m("Camaro", SPORTS, 36_000, 1990, 2024),
            m("Corvette", SPORTS, 72_000, 1990), m("Trax", COMPACT_SUV, 23_000, 2015),
            m("Equinox", COMPACT_SUV, 31_000, 2005), m("Traverse", MIDSIZE_SUV, 41_000, 2009),
            m("Tahoe", FULLSIZE_SUV, 62_000, 1995), m("Suburban", FULLSIZE_SUV, 66_000, 1990),
            m("Colorado", PICKUP, 36_000, 2004), m("Silverado 1500", PICKUP, 50_000, 1999),
            m("Silverado 2500HD", PICKUP, 58_000, 2001), m("Bolt EV", ELECTRIC, 29_000, 2017),
            m("Equinox EV", ELECTRIC, 36_000, 2024),
        )),
        Make("Chrysler", 1.15, listOf(
            m("300", FULLSIZE, 38_000, 2005, 2023), m("Pacifica", MINIVAN, 45_000, 2017),
            m("Town & Country", MINIVAN, 36_000, 1990, 2016),
        )),
        Make("Dodge", 1.08, listOf(
            m("Charger", FULLSIZE, 40_000, 2006), m("Challenger", SPORTS, 40_000, 2008, 2023),
            m("Durango", MIDSIZE_SUV, 44_000, 1998), m("Grand Caravan", MINIVAN, 32_000, 1990, 2020),
            m("Hornet", COMPACT_SUV, 32_000, 2023),
        )),
        Make("Ford", 1.02, listOf(
            m("Fiesta", ECONOMY, 18_000, 2011, 2019), m("Focus", ECONOMY, 21_000, 2000, 2018),
            m("Fusion", MIDSIZE, 27_000, 2006, 2020), m("Taurus", FULLSIZE, 32_000, 1990, 2019),
            m("Mustang", SPORTS, 38_000, 1990), m("Mustang Mach-E", ELECTRIC, 46_000, 2021),
            m("Escape", COMPACT_SUV, 31_000, 2001), m("Bronco Sport", COMPACT_SUV, 33_000, 2021),
            m("Bronco", MIDSIZE_SUV, 46_000, 1990, 1996), m("Bronco", MIDSIZE_SUV, 46_000, 2021),
            m("Edge", MIDSIZE_SUV, 39_000, 2007, 2024), m("Explorer", MIDSIZE_SUV, 42_000, 1991),
            m("Expedition", FULLSIZE_SUV, 64_000, 1997), m("Ranger", PICKUP, 36_000, 1990, 2011),
            m("Ranger", PICKUP, 36_000, 2019), m("Maverick", PICKUP, 28_000, 2022),
            m("F-150", PICKUP, 52_000, 1990), m("F-250 Super Duty", PICKUP, 60_000, 1999),
            m("F-150 Lightning", ELECTRIC, 62_000, 2022),
        )),
        Make("GMC", 1.00, listOf(
            m("Terrain", COMPACT_SUV, 33_000, 2010), m("Acadia", MIDSIZE_SUV, 45_000, 2007),
            m("Yukon", FULLSIZE_SUV, 66_000, 1992), m("Canyon", PICKUP, 40_000, 2004),
            m("Sierra 1500", PICKUP, 55_000, 1999), m("Sierra 2500HD", PICKUP, 62_000, 2001),
            m("Hummer EV", ELECTRIC, 98_000, 2022),
        )),
        Make("Honda", 0.85, listOf(
            m("Civic", ECONOMY, 27_000, 1990), m("Accord", MIDSIZE, 31_000, 1990),
            m("Fit", ECONOMY, 19_000, 2007, 2020), m("HR-V", COMPACT_SUV, 27_000, 2016),
            m("CR-V", COMPACT_SUV, 34_000, 1997), m("Passport", MIDSIZE_SUV, 44_000, 1994),
            m("Pilot", MIDSIZE_SUV, 45_000, 2003), m("Odyssey", MINIVAN, 42_000, 1995),
            m("Ridgeline", PICKUP, 43_000, 2006), m("Prologue", ELECTRIC, 49_000, 2024),
        )),
        Make("Hyundai", 1.02, listOf(
            m("Elantra", ECONOMY, 23_000, 1992), m("Sonata", MIDSIZE, 28_000, 1990),
            m("Kona", COMPACT_SUV, 26_000, 2018), m("Tucson", COMPACT_SUV, 31_000, 2005),
            m("Santa Fe", MIDSIZE_SUV, 37_000, 2001), m("Palisade", MIDSIZE_SUV, 43_000, 2020),
            m("Ioniq 5", ELECTRIC, 47_000, 2022), m("Ioniq 6", ELECTRIC, 45_000, 2023),
        )),
        Make("Infiniti", 1.18, listOf(
            m("Q50", LUXURY, 45_000, 2014), m("G35 / G37", LUXURY, 40_000, 2003, 2013),
            m("QX50", LUXURY_SUV, 46_000, 2014), m("QX60", LUXURY_SUV, 52_000, 2013),
            m("QX80", FULLSIZE_SUV, 85_000, 2011),
        )),
        Make("Jeep", 0.95, listOf(
            m("Wrangler", MIDSIZE_SUV, 42_000, 1990), m("Grand Cherokee", MIDSIZE_SUV, 46_000, 1993),
            m("Cherokee", COMPACT_SUV, 33_000, 1990), m("Compass", COMPACT_SUV, 30_000, 2007),
            m("Renegade", COMPACT_SUV, 27_000, 2015, 2023), m("Gladiator", PICKUP, 44_000, 2020),
            m("Wagoneer", FULLSIZE_SUV, 70_000, 2022),
        )),
        Make("Kia", 1.00, listOf(
            m("Forte", ECONOMY, 22_000, 2010), m("K5 / Optima", MIDSIZE, 28_000, 2001),
            m("Soul", ECONOMY, 22_000, 2010), m("Seltos", COMPACT_SUV, 26_000, 2021),
            m("Sportage", COMPACT_SUV, 31_000, 1995), m("Sorento", MIDSIZE_SUV, 36_000, 2003),
            m("Telluride", MIDSIZE_SUV, 44_000, 2020), m("Carnival / Sedona", MINIVAN, 40_000, 2002),
            m("EV6", ELECTRIC, 48_000, 2022), m("EV9", ELECTRIC, 60_000, 2024),
        )),
        Make("Land Rover", 1.20, listOf(
            m("Range Rover", LUXURY_SUV, 110_000, 1990), m("Range Rover Sport", LUXURY_SUV, 85_000, 2006),
            m("Range Rover Evoque", LUXURY_SUV, 52_000, 2012), m("Discovery", LUXURY_SUV, 62_000, 1994),
            m("Defender", LUXURY_SUV, 65_000, 2020),
        )),
        Make("Lexus", 0.85, listOf(
            m("IS", LUXURY, 43_000, 2001), m("ES", LUXURY, 45_000, 1990),
            m("LS", LUXURY, 82_000, 1990), m("NX", LUXURY_SUV, 45_000, 2015),
            m("RX", LUXURY_SUV, 52_000, 1999), m("GX", LUXURY_SUV, 66_000, 2003),
            m("LX", FULLSIZE_SUV, 100_000, 1996),
        )),
        Make("Lincoln", 1.15, listOf(
            m("MKZ", LUXURY, 38_000, 2007, 2020), m("Corsair", LUXURY_SUV, 42_000, 2020),
            m("Nautilus", LUXURY_SUV, 52_000, 2019), m("Aviator", LUXURY_SUV, 60_000, 2020),
            m("Navigator", FULLSIZE_SUV, 92_000, 1998),
        )),
        Make("Mazda", 0.95, listOf(
            m("Mazda3", ECONOMY, 26_000, 2004), m("Mazda6", MIDSIZE, 28_000, 2003, 2021),
            m("MX-5 Miata", SPORTS, 32_000, 1990), m("CX-30", COMPACT_SUV, 28_000, 2020),
            m("CX-5", COMPACT_SUV, 32_000, 2013), m("CX-50", COMPACT_SUV, 34_000, 2023),
            m("CX-9", MIDSIZE_SUV, 42_000, 2007, 2023), m("CX-90", MIDSIZE_SUV, 45_000, 2024),
        )),
        Make("Mercedes-Benz", 1.18, listOf(
            m("C-Class", LUXURY, 50_000, 1994), m("E-Class", LUXURY, 64_000, 1994),
            m("S-Class", LUXURY, 120_000, 1990), m("GLA", LUXURY_SUV, 44_000, 2015),
            m("GLC", LUXURY_SUV, 52_000, 2016), m("GLE / M-Class", LUXURY_SUV, 64_000, 1998),
            m("GLS", LUXURY_SUV, 90_000, 2007), m("G-Class", LUXURY_SUV, 150_000, 2002),
            m("EQE", ELECTRIC, 78_000, 2023), m("Sprinter", MINIVAN, 55_000, 2003),
        )),
        Make("Mini", 1.08, listOf(
            m("Cooper", ECONOMY, 30_000, 2002), m("Countryman", COMPACT_SUV, 38_000, 2011),
        )),
        Make("Mitsubishi", 1.12, listOf(
            m("Mirage", ECONOMY, 18_000, 1990), m("Lancer", ECONOMY, 22_000, 2002, 2017),
            m("Outlander", COMPACT_SUV, 32_000, 2003), m("Outlander Sport", COMPACT_SUV, 26_000, 2011),
            m("Eclipse Cross", COMPACT_SUV, 28_000, 2018),
        )),
        Make("Nissan", 1.08, listOf(
            m("Versa", ECONOMY, 19_000, 2007), m("Sentra", ECONOMY, 23_000, 1990),
            m("Altima", MIDSIZE, 29_000, 1993), m("Maxima", FULLSIZE, 40_000, 1990, 2023),
            m("370Z / Z", SPORTS, 45_000, 2009), m("Kicks", COMPACT_SUV, 23_000, 2018),
            m("Rogue", COMPACT_SUV, 32_000, 2008), m("Murano", MIDSIZE_SUV, 40_000, 2003),
            m("Pathfinder", MIDSIZE_SUV, 41_000, 1990), m("Armada", FULLSIZE_SUV, 60_000, 2004),
            m("Frontier", PICKUP, 35_000, 1998), m("Titan", PICKUP, 48_000, 2004, 2024),
            m("Leaf", ELECTRIC, 32_000, 2011), m("Ariya", ELECTRIC, 45_000, 2023),
        )),
        Make("Porsche", 0.88, listOf(
            m("911", SPORTS, 125_000, 1990), m("718 Cayman / Boxster", SPORTS, 75_000, 1997),
            m("Macan", LUXURY_SUV, 65_000, 2015), m("Cayenne", LUXURY_SUV, 85_000, 2003),
            m("Panamera", LUXURY, 105_000, 2010), m("Taycan", ELECTRIC, 105_000, 2020),
        )),
        Make("Ram", 1.00, listOf(
            m("1500", PICKUP, 52_000, 1994), m("2500", PICKUP, 60_000, 1994),
            m("ProMaster", MINIVAN, 48_000, 2014),
        )),
        Make("Subaru", 0.88, listOf(
            m("Impreza", ECONOMY, 25_000, 1993), m("WRX", SPORTS, 36_000, 2002),
            m("Legacy", MIDSIZE, 29_000, 1990), m("BRZ", SPORTS, 33_000, 2013),
            m("Crosstrek", COMPACT_SUV, 29_000, 2013), m("Forester", COMPACT_SUV, 33_000, 1998),
            m("Outback", MIDSIZE_SUV, 35_000, 1995), m("Ascent", MIDSIZE_SUV, 42_000, 2019),
            m("Solterra", ELECTRIC, 46_000, 2023),
        )),
        Make("Tesla", 1.10, listOf(
            m("Model 3", ELECTRIC, 42_000, 2017), m("Model Y", ELECTRIC, 46_000, 2020),
            m("Model S", ELECTRIC, 82_000, 2012), m("Model X", ELECTRIC, 88_000, 2016),
            m("Cybertruck", ELECTRIC, 82_000, 2024),
        )),
        Make("Toyota", 0.82, listOf(
            m("Corolla", ECONOMY, 24_000, 1990), m("Camry", MIDSIZE, 30_000, 1990),
            m("Avalon", FULLSIZE, 40_000, 1995, 2022), m("Prius", ECONOMY, 30_000, 2001),
            m("GR86 / 86", SPORTS, 32_000, 2017), m("Supra", SPORTS, 56_000, 1993, 1998),
            m("Supra", SPORTS, 56_000, 2020), m("C-HR", COMPACT_SUV, 25_000, 2018, 2022),
            m("Corolla Cross", COMPACT_SUV, 28_000, 2022), m("RAV4", COMPACT_SUV, 33_000, 1996),
            m("Highlander", MIDSIZE_SUV, 44_000, 2001), m("Grand Highlander", MIDSIZE_SUV, 48_000, 2024),
            m("4Runner", MIDSIZE_SUV, 46_000, 1990), m("Sequoia", FULLSIZE_SUV, 68_000, 2001),
            m("Land Cruiser", FULLSIZE_SUV, 60_000, 1990), m("Sienna", MINIVAN, 43_000, 1998),
            m("Tacoma", PICKUP, 38_000, 1995), m("Tundra", PICKUP, 52_000, 2000),
            m("bZ4X", ELECTRIC, 44_000, 2023),
        )),
        Make("Volkswagen", 1.08, listOf(
            m("Jetta", ECONOMY, 24_000, 1990), m("Golf / GTI", ECONOMY, 32_000, 1990),
            m("Passat", MIDSIZE, 29_000, 1990, 2022), m("Beetle", ECONOMY, 23_000, 1998, 2019),
            m("Taos", COMPACT_SUV, 28_000, 2022), m("Tiguan", COMPACT_SUV, 32_000, 2009),
            m("Atlas", MIDSIZE_SUV, 42_000, 2018), m("ID.4", ELECTRIC, 43_000, 2021),
        )),
        Make("Volvo", 1.12, listOf(
            m("S60", LUXURY, 46_000, 2001), m("S90", LUXURY, 58_000, 2017),
            m("XC40", LUXURY_SUV, 42_000, 2019), m("XC60", LUXURY_SUV, 50_000, 2010),
            m("XC90", LUXURY_SUV, 60_000, 2003), m("EX30 / EX40", ELECTRIC, 45_000, 2024),
        )),
    ).sortedBy { it.name }

    fun findMake(name: String): Make? = makes.firstOrNull { it.name.equals(name, ignoreCase = true) }

    /** Makes that sold at least one model in [year]. */
    fun makesFor(year: Int): List<Make> = makes.filter { make -> make.models.any { it.isAvailable(year) } }

    /** Models of [make] sold in [year], de-duplicated by name. */
    fun modelsFor(make: Make, year: Int): List<ModelSpec> =
        make.models.filter { it.isAvailable(year) }.distinctBy { it.name }.sortedBy { it.name }

    fun years(currentYear: Int): List<Int> = (currentYear + 1 downTo EARLIEST_YEAR).toList()
}
