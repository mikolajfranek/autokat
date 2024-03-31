import { MMKV } from 'react-native-mmkv';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';

const storage = new MMKV();

export function getLocalStorageString(key: LocalStorageKeys): string {
    let value = storage.getString(key.toString());
    return value == undefined ? '' : value;
}

export function getLocalStorageBoolean(key: LocalStorageKeys): boolean {
    let value = storage.getBoolean(key.toString());
    return value == undefined ? false : value;
}

export function setLocalStorage(key: LocalStorageKeys, value: string | number | boolean | Uint8Array): void {
    storage.set(key.toString(), value);
}