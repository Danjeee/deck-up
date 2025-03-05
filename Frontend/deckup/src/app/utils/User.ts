export class User{
    id: number = -1
    username: string = ""
    email: string = ""
    pfp: string = ""
    currency: number = 0
    estado: string = ""
    rol: any[] = []
    constructor(id: number, username:string, email:string, pfp:string, currency:number, rol:any[]){
        this.id = id
        this.username = username
        this.email = email
        this.pfp = pfp
        this.currency = currency
        this.rol = rol
    }
}
