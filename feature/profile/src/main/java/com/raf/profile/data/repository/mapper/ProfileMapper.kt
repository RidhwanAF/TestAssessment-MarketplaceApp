package com.raf.profile.data.repository.mapper

import com.raf.core.domain.model.Profile
import com.raf.core.domain.model.ProfileAddress
import com.raf.core.domain.model.ProfileGeolocation
import com.raf.core.domain.model.ProfileName
import com.raf.profile.data.local.room.AddressEntity
import com.raf.profile.data.local.room.GeolocationEntity
import com.raf.profile.data.local.room.NameEntity
import com.raf.profile.data.local.room.ProfileEntity
import com.raf.profile.data.remote.response.AddressResponse
import com.raf.profile.data.remote.response.GeolocationResponse
import com.raf.profile.data.remote.response.NameResponse
import com.raf.profile.data.remote.response.ProfileResponse

object ProfileMapper {

    // Entity
    fun ProfileResponse.toEntity() = ProfileEntity(
        id = id,
        username = username,
        email = email,
        password = password,
        name = name.toEntity(),
        phone = phone,
        address = address.toEntity(),
    )

    private fun NameResponse.toEntity() = NameEntity(
        firstname = firstname,
        lastname = lastname,
    )

    private fun AddressResponse.toEntity() = AddressEntity(
        geolocation = geolocation.toEntity(),
        city = city,
        street = street,
        number = number,
        zipcode = zipcode,
    )

    private fun GeolocationResponse.toEntity() = GeolocationEntity(
        lat = lat,
        long = long,
    )

    // Domain
    fun ProfileEntity.toDomain() = Profile(
        id = id,
        username = username,
        email = email,
        password = password,
        name = name.toDomain(),
        phone = phone,
        address = address.toDomain(),
    )

    private fun NameEntity.toDomain() = ProfileName(
        firstname = firstname,
        lastname = lastname,
    )

    private fun AddressEntity.toDomain() = ProfileAddress(
        geolocation = geolocation.toDomain(),
        city = city,
        street = street,
        number = number,
        zipcode = zipcode,
    )

    private fun GeolocationEntity.toDomain() = ProfileGeolocation(
        lat = lat,
        long = long,
    )
}