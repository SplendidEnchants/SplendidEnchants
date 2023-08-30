package world.icebear03.splendidenchants.data

import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import world.icebear03.splendidenchants.Config

object SqlHandler {

    val host = HostSQL(Config.config.getConfigurationSection("database")!!)

    val table = Table("se_player_data", host) {
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 255) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("filters") {

        }
        add("ticker_recorder") {

        }
        add("favorite_enchants") {

        }
        add("cooldown") {

        }
    }

    val dataSource = host.createDataSource()

    fun load() {
        table.workspace(dataSource) { createTable(true) }.run()
    }

//    fun get(uuid: UUID)
}