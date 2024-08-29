import { MMKV } from 'react-native-mmkv';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { getPrivateKey } from '../APIGoogle/Common';

const storage = new MMKV({ id: 'mmkv.default', encryptionKey: getPrivateKey().substring(28) });

export function getLocalStorageString(key: LocalStorageKeys): string {
    const value = getLocalStorageStringOrUndefined(key);
    return value == undefined ? '' : value;
}

export function getLocalStorageStringOrUndefined(key: LocalStorageKeys): string | undefined {
    return storage.getString(key.toString());
}

export function getLocalStorageBoolean(key: LocalStorageKeys): boolean {
    const value = getLocalStorageBooleanOrUndefined(key);
    return value == undefined ? false : value;
}

export function getLocalStorageBooleanOrUndefined(key: LocalStorageKeys): boolean | undefined {
    return storage.getBoolean(key.toString());
}

export function setLocalStorage(key: LocalStorageKeys, value: string | number | boolean | Uint8Array): void {
    storage.set(key.toString(), value);
}

export function deleteLocalStorage(key: LocalStorageKeys): void {
    storage.delete(key.toString());
}