import { MMKV } from 'react-native-mmkv';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { getPrivateKey } from '../APIGoogle/Common';

const storage = new MMKV({ id: 'mmkv.default', encryptionKey: getPrivateKey() });

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