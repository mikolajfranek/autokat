import { MetalSymbol } from './MetalSymbol';

export enum Metal {
    platinum,
    palladium,
    rhodium,
};

export function getSymbol(metal: Metal): MetalSymbol {
    switch (metal) {
        case Metal.platinum:
            return MetalSymbol.pt;
        case Metal.palladium:
            return MetalSymbol.pd;
        case Metal.rhodium:
            return MetalSymbol.rh;
        default:
            throw TypeError();
    }
}