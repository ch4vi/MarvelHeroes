package com.xavi.marvelheroes.data.mapper

import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.utils.DBMapper

class CharacterDBMapper : DBMapper<CharacterDomainModel, CharacterDB> {

    companion object {
        const val NAME = "character_db_mapper"
    }

    override fun mapToDB(domainModel: CharacterDomainModel) = CharacterDB(
        id = domainModel.id,
        name = domainModel.name,
        description = domainModel.description,
        thumbnail = domainModel.thumbnail
    )

    override fun mapToDomain(dbModel: CharacterDB) = CharacterDomainModel(
        id = dbModel.id,
        name = dbModel.name,
        description = dbModel.description,
        thumbnail = dbModel.thumbnail
    )
}
