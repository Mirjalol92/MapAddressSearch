package com.example.locationsample.data.model

import android.text.TextUtils
import android.widget.TextView
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AddressResponse(
    @SerializedName("meta") var meta : Meta,
    @SerializedName("documents") var documents : MutableList<Document>
)

data class Meta(
    @SerializedName("total_count") var total_count : Int = 0,                                //검색어에 검색된 문서 수
    @SerializedName("pageable_count") var pageable_count : Int = 0,                          //total_count 중 노출 가능 문서 수, 최대 45
    @SerializedName("is_end") var is_end : Boolean = true,                                   //현재 페이지가 마지막 페이지인지 여부 : 값이 false이면 page를 증가시켜 다음 페이지 요청 가능
    @SerializedName("same_name") var same_name : RegionInfo = RegionInfo()                   //질의어의 지역 및 키워드 분석 정보 -> '/v2/local/search/keyword'에서만 쓰이며 유사 키워드 리스트를 보여주기 위한 기능
){
    data class RegionInfo(
        @SerializedName("keyword") var keyword : String = "",                                //질의어에서 지역 정보를 제외한 키워드 : ex) '중앙로 맛집' 에서 '맛집'
        @SerializedName("selected_region") var selected_region : String = "",                //인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
        @SerializedName("region") var region : List<String> = listOf()                       //질의어에서 인식된 지역의 리스트 : ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    )
}

data class Document(
    @SerializedName("address_name") var address_name : String = "",                          //전체 지번 주소
    @SerializedName("road_address_name") var road_address_name : String = "",                //전체 도로명 주소 : only use 키워드 검색
    @SerializedName("place_name") var place_name : String = "",                              //장소명, 업체명 : only use 키워드 검색
    @SerializedName("road_address") var road_address : RoadAddress = RoadAddress(),          //도로명 주소 상세 정보 : only use 좌표로 주소 변환
    @SerializedName("address") var address : Address = Address(),                            //지번 주소 상세 정보
    @SerializedName("x") var x : Double = 0.0,                                               //X 좌표값 혹은 longitude
    @SerializedName("y") var y : Double = 0.0                                                //Y 좌표값 혹은 latitude
) : Serializable {
    data class RoadAddress(
        @SerializedName("address_name") var address_name : String = "",                      //전체 도로명 주소
        @SerializedName("building_name") var building_name : String = ""                     //건물 이름
    ) : Serializable

    data class Address(
        @SerializedName("address_name") var address_name : String = ""                       //전체 지번 주소
    ) : Serializable

    fun containsIgnoreArea(area: String): Boolean{
        return address_name.contains(area) ||
                road_address_name.contains(area) ||
                place_name.contains(area)
    }

    val prettyAddress: Pair<String, String>
        get() {
            if (!TextUtils.isEmpty(place_name)) {
                return Pair(place_name, road_address_name)
            } else {
                if (!TextUtils.isEmpty(road_address_name)) {
                    return Pair(road_address_name, "")
                } else if(!TextUtils.isEmpty(address_name)){
                    return Pair(address_name, "")
                }else if (road_address != null){
                    if(!TextUtils.isEmpty(road_address.building_name)){
                        return Pair(road_address.building_name, road_address.address_name)
                    }else{
                        return Pair("", road_address.address_name)
                    }
                }else{
                    return Pair("","")
                }
            }
        }

    fun noLocationDate(): Boolean = (x == 0.0) && (y == 0.0)
}