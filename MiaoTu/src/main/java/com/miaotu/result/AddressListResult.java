package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.Address;
import com.miaotu.model.BlackInfo;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class AddressListResult extends BaseResult {
    @JsonProperty("Items")
    private List<Address> addressList;

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
}
