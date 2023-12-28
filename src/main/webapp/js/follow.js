/**
 *
 */
const fb = document.getElementById('follow');
const ufb = document.getElementById('unfollow');
const id = document.getElementById('employeeId');
const code = document.getElementById('employeeCode');
const fs = document.getElementById('followState');

//フォローボタンの初期設定
if(Number(fs.innerText)){
    ufb.classList.remove("hidden");
    fb.classList.add("hidden");
}else{
    fb.classList.remove("hidden");
    ufb.classList.add("hidden");
}

const show_var = (myConst) => {
  console.log("JSで受けっとった値: " + myConst);
};

fb.addEventListener('click', async (e) =>{
    const connect = await new HttpConnection(`?action=Follow&command=follow&id=${id.innerText}`).toGetFetch();
    if(connect.status === 200){
        ufb.classList.remove("hidden");
        fb.classList.add("hidden");
    }
    //const test = await new Follow().test();
    //location.href = "?action=Employee&command=index";
});

ufb.addEventListener('click', async (e) =>{
    const connect = await new HttpConnection(`?action=Follow&command=unfollow&id=${id.innerText}`).toGetFetch();
    if(connect.status === 200){
        fb.classList.remove("hidden");
        ufb.classList.add("hidden");
    //const test = await new Follow().test();
    //location.href = "?action=Employee&command=index";
    }
});


class HttpConnection{
    constructor(url){
        this.url = url;
    }

    async toGetConnect(){
        const httpTest = new XMLHttpRequest();
        httpTest.open('GET', this.url);
        await httpTest.send();
        httpTest.onload = () =>{
        }
        return httpTest;
    }

    async toGetFetch(){
        try{
            const response = await fetch(this.url);
            return response;
        }catch(e){
            return "error";
        }
    }
}


/*const mysql = require('mysql');
 const connection = mysql.createConnection({
    host : 'localhost',
    user : 'repuser',
    password : 'reppass',
    database : 'daily_report_system'
});

 console.log(`${connection.host}、${connection.user}、${connection.password}、${connection.database}`);

connection.connect((err) =>{
     console.log("接続の動作しました");
    if(err){
        console.error(`Error!: ${err.stack}`);
        return;
    }

    console.log(`Success! Connected as id${connection.threadId}`);
});

connection.end();
*/