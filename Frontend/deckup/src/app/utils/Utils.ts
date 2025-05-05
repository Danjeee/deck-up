export class Utils {

    public static msg: any = {
       vida: "Vida",
       stun: "Aturdido",
       stun_name: "Tipo de aturdimiento",
       burn: "Quemado",
       poisn: "Envenenado",
       bleed: "Sangrado",
       prcnt_up: "Mejora de daño",
       prcnt_dwn: "Debilidad", 

       hab_freeze: "Aturdimiento",
       hab_freeze_name: "Tipo de aturdimiento",
       hab_burn: "Quemado",
       hab_heal: "Curación",
       hab_poisn: "Envenenado",
       hab_bleed: "Sangrado",
       hab_prcnt_up: "Mejora de daño",
       hab_prcnt: "Daño de porcentaje",
       hab_prcnt_dwn: "Debilidad", 
       hab_crit: "Porcentaje de critico",
       hab_critMult: "Mejora de daño critico",
       hab_leth: "Letalidad",
       hab_esq: "Porcentaje de evasión",
       hab_load_atq: "wip",
       hab_dmg: "Daño"
    }

    public static css(target: HTMLElement, options: any) {
        Object.keys(options).forEach((key: any) => {
            target.style[key] = options[key]
        });
    }

}