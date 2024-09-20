import axios from "axios";

function post(url, data, ) {
    axios.post(url, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true // 是否携带cookie??

    })
}