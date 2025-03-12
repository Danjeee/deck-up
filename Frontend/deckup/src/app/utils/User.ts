import { Timestamp } from "rxjs"

export class User{
    id: number = -1
    username: string = ""
    email: string = ""
    pfp: string = ""
    currency: number = 0
    estado: string = ""
    auth: string = ""
    rol: any[] = []
    pay: Date = new Date()
    constructor(id: number, username:string, email:string, pfp:string, currency:number, rol:any[], pay:string, auth?: string){
        this.id = id
        this.username = username
        this.email = email
        this.pfp = pfp
        this.currency = currency
        this.rol = rol
        this.pay = new Date(pay)
        auth? this.auth = auth : {}
    }
}
