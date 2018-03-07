
function loginMQ(){
    var adm_name=document.getElementById("lname").value;
    var adm_pwd=document.getElementById("lpwd").value;
    if (adm_name==='lkx'&&adm_pwd ==='1'){
        alert("登录成功");
        window.location.href='http://127.0.0.1:8080/index.html';
    }else{
        alert("登录失败！请检查您输入的账号和密码是否正确");
    }
}