package com.xavi.marvelheroes.data.model

import com.xavi.marvelheroes.domain.model.PageDomainModel
import com.xavi.marvelheroes.domain.utils.DTO
import com.xavi.marvelheroes.domain.utils.Mapper

class PageMapper<T : DTO> : Mapper<PageDomainModel, PageDTO<T>> {
    override fun map(dto: PageDTO<T>): PageDomainModel {
        val offset = dto.offset ?: throw IllegalArgumentException("offset")
        val limit = dto.limit ?: throw IllegalArgumentException("limit")
        val total = dto.total ?: throw IllegalArgumentException("total")
        val count = dto.count ?: throw IllegalArgumentException("count")
        return PageDomainModel(offset, limit, total, count)
    }
}
