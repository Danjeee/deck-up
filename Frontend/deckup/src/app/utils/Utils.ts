import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Localizer {

    private msg: any;
    private lngs: any;
    private hasLng: boolean = false;

    get(key: string, fallback: string = ""){
        if (this.msg[key] != undefined){
            return this.msg[key];
        } else {
            return fallback;
        }
    }
    /**
     * Set translator
     * @param translator - set the translator as a json
     * @param lngs - 
     */
    set(translator:{}, lngs: {} | null = null) {
        this.msg = translator
        if (lngs != null){
            this.hasLng = true
            this.lngs = lngs
        }
    }

}

/**
 * Function made to apply css easily
 * 
 * @param target - HTMLElement or NodeListOf<HTMLElement> to edit
 * @param options - css to apply
 * 
 * NOTE: Use style names like js does (ex. use pointerEvents instead of pointer-events)
 * EX. css(document.getElementById('target') as HTMLElement,{ bacgroundColor: "#fff"})
 * EX2. css(document.querySelectorAll('.style'),{ bacgroundColor: "#000", position: "fixed"})
 */
export function css(target: HTMLElement | NodeListOf<HTMLElement>, options: any) {
    if (target instanceof NodeList){
        target.forEach((elem: HTMLElement) => {
            Object.keys(options).forEach((key: any) => {
            elem.style[key] = options[key]
        });
        })
    } else {
        Object.keys(options).forEach((key: any) => {
        target.style[key] = options[key]
        });
    }
}