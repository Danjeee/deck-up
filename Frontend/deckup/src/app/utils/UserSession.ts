export class UserSession{
    public static setUser(user:any){
        localStorage.setItem("user", JSON.stringify(user))
    }
    public static getUser(){
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            return JSON.parse(sessionuser)
        } else {
            return "Guest"
        }
        
    }
    public static getId(){
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            return JSON.parse(sessionuser).id
        } else {
            return "-1"
        }
        
    }
    public static logOut(){
        localStorage.setItem("user", "0")
    }
    public static getRole(){
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            var user_json = JSON.parse(sessionuser)
            return user_json.rol[0].nombre
        } else {
            return "Guest"
        }
    }
    
}