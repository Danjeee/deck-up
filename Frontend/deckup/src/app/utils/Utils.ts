import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class Localizer {

    private msg: any;
    private lngs: any;
    private hasLng: boolean = false;

    getLngs() {
        return this.lngs;
    }

    add(translator: {}, lng: string | null = null) {
        if (lng == null) {
            let aux = { ...this.msg, ...translator }
            this.msg = aux;
        } else {
            let aux = { ...this.msg[lng], ...translator }
            this.msg[lng] = aux;
        }
    }

    addLngs(newlangs: {}) {
        Object.keys(newlangs).forEach((k: any) => {
            this.msg[k] = {}
        })
        let aux = { ...this.lngs, ...newlangs }
        this.lngs = newlangs
        this.hasLng = true
    }

    get(key: string, lng: string | null = null, fallback: string = "", force: boolean = false) {
        let aux = this.msg
        if (force) {
            if (!va(lng)) {
                Object.keys(aux).forEach((k: string) => {
                    aux[k.toLowerCase()] = aux[k]
                })
                key = key.toLowerCase();
            } else {
                Object.keys(aux).forEach((k: string) => {
                    if (aux.length) {
                        Object.keys(aux[k]).forEach((k2: string) => {
                            aux[k][k2.toLowerCase()] = aux[k][k2]
                        })
                        aux[k.toLowerCase()] = aux[k]
                    } else {
                        aux[k.toLowerCase()] = aux[k] 
                    }
                })
                key = key.toLowerCase();
                lng = (lng as string).toLowerCase()
            }
        }
        if (!va(lng)) {
            if (aux[key] != undefined) {
                return aux[key];
            } else {
                return fallback;
            }
        } else {
            lng = lng as string
            if (!va(aux[lng])) {
                console.error("Language does not exist")
                return fallback;
            }
            if (aux[lng][key] != undefined) {
                return aux[lng][key];
            } else {
                return fallback;
            }
        }
    }
    /**
     * Set translator
     * @param translator - set the translator as a json
     * @param lngs - 
     */
    set(translator: any, lngs: {} | null = null) {
        this.msg = {};
        if (lngs != null) {
            this.hasLng = true
            this.lngs = lngs
            Object.keys(lngs).forEach((k: any) => {
                this.msg[k] = translator[k];
            })
        } else {
            this.msg = translator
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
    if (target instanceof NodeList) {
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

/**
 * 
 * @param item 
 * @returns true if not empty
 */
export function va(item: any) {
    if (item == null) {
        return false
    }
    if (item == undefined) {
        return false
    }
    if ((item + "").replace(" ", "") == "") {
        return false
    }
    return true
}

export class DOM {
    public static new<K extends keyof HTMLElementTagNameMap>(tag: K, id: string | null = null, parent: string | null = null): HTMLElementTagNameMap[K] {
        const elem = document.createElement(tag);
        if (va(id)) {
            elem.id = id as string
        }
        if (va(parent)) {
            const parentElem = DOM.g(parent as string)
            if (parentElem instanceof NodeList) {
                parentElem.forEach((e: any) => {
                    (e as HTMLElement).appendChild(elem)
                })
            } else {
                (parentElem as HTMLElement).appendChild(elem)
            }
        }
        return elem;
    }
    public static g(selector: string) {
        return document.querySelectorAll(selector)
    }
    public static get(selector: string) {
        return document.querySelectorAll(selector)
    }
}

export function $$(selector: string) {
    const elems = document.querySelectorAll(selector)
    switch (elems.length) {
        case 0:
            return null;
        case 1:
            return elems[0];
        default:
            return elems;
    }
}

interface HeaderBasicConfig {
    customClass?: string;
    height?: string;
    primaryColor?: string
    secondaryColor?: string;
    background?: string;
    title?: string;
    logo?: string;
    position?: "fixed" | "relative" | "absolute";
    textColor?: string;
    font?: string;
    fontSize?: string;
    alignment?: "top" | "center" | "bottom";
    justify?: "left" | "center" | "right" | "around" | "between";
    display?: "flex" | "grid"
}

export function generateHeader(basic: HeaderBasicConfig = {}) {
    const t: Localizer = new Localizer();
    t.set({
        B: {
            left: "start",
            right: "end",
            around: "space-around",
            between: "space-between",
            bottom: "end",
        }
    }, {
        B: "basic"
    })
    const def: Required<HeaderBasicConfig> = {
        customClass: "",
        height: "10dvh",
        primaryColor: "#FFF",
        secondaryColor: "#F8F8F8",
        background: "#ffffff",
        title: "",
        logo: "",
        position: "fixed",
        textColor: "#000",
        font: "",
        fontSize: "24px",
        alignment: "center",
        justify: "center",
        display: "flex"
    }
    const b = { ...def, ...basic }
    const header = DOM.new("header", "header", "body")
    header.className = b.customClass;
    css(header, {
        width: "100dvw",
        height: b.height,
        position: b.position,
        top: "0",
        left: "0",
        color: b.textColor,
        font: b.font,
        fontSize: b.fontSize,
        display: b.display,
        alignItems: t.get(b.alignment, "B", b.alignment),
        justifyContent: t.get(b.justify, "B", b.justify)
    });
    if (b.background?.startsWith("#")) {
        css(header, {
            backgroundColor: b.background,
        })
    }
    if (va(b.title)) {
        const tit = DOM.new("h1", "title", "#header")
        css(tit, {
            margin: "0",
            padding: "0"
        })
        tit.innerHTML = b.title
    }
}