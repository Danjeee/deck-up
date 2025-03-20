import { HttpClient } from "@angular/common/http"
import { UserService } from "../services/user.service"
import { User } from "./User"

export class UserSession {


    constructor(service: UserService) { }

    public static setUser(user: any) {
        localStorage.setItem("user", JSON.stringify(user))
    }
    public static getUser() {
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            return JSON.parse(sessionuser)
        } else {
            return "Guest"
        }

    }
    public static getId() {
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            return JSON.parse(sessionuser).id
        } else {
            return "-1"
        }

    }
    public static logOut() {
        localStorage.setItem("user", "0")
    }
    public static getRole() {
        var sessionuser = localStorage.getItem("user")
        if (sessionuser != null && sessionuser != "0") {
            var user_json = JSON.parse(sessionuser)
            return user_json.rol[0].nombre
        } else {
            return "Guest"
        }
    }
    public static wasLoggedAs(email: string): boolean {
        console.log(localStorage.getItem("past_users"))
        if (localStorage.getItem("past_users") == null) {
            localStorage.setItem("past_users", "")
        }
        const users = localStorage.getItem("past_users")?.split(",") as string[]
        if (users.includes(email)) {
            return true;
        } else {
            return false;
        }

    }
    public static addToPastUsers(email: string): void {
        if (localStorage.getItem("past_users") == null) {
            localStorage.setItem("past-users", "")
        }
        var users = localStorage.getItem("past_users") as string
        users += email + ","
        localStorage.setItem("past_users", users)

    }

    static pay(cant: number) {
        var sessionuser = localStorage.getItem("user") as string
        let user = JSON.parse(sessionuser)
        user.currency = user.currency - cant
        this.setUser(user)
        const currency = document.getElementById("currency_txt") as HTMLElement
        for (let i = Number.parseInt(currency.innerHTML); i > (UserSession.getUser().currency as number); i--) {
            setTimeout(() => {
                currency.innerHTML = (Number.parseInt(currency.innerHTML) - 1) + ""
            }, 50);
        }

    }

}