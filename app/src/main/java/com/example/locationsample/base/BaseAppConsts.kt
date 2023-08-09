package com.example.locationsample.base

import com.example.locationsample.data.model.Document

object BaseAppConsts {
    const val REST_API_BASE                                                                         ="https://dapi.kakao.com/v2/local/"
    const val REST_API_ADDRESS_URL                                                                  = "search/address.json"
    const val REST_API_LOCAL_URL                                                                    = "search/keyword.json"
    const val REST_API_COORD_TO_ADDRESS_URL                                                         = "geo/coord2address.json"
    val DEFUALT_START_LOCATION                                                                      = Document(place_name = "테크노마트", address_name = "서울 광진구 구의동 546-4",road_address_name = "서울 광진구 광나루로56길 85", x = 127.095864582406, y = 37.5359820922893)

}