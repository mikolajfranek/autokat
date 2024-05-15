import Realm, { ObjectSchema, Types } from "realm";

export default class Filter extends Realm.Object<Filter> {
    _id!: Types.Int;
    name!: Types.String;

    static schema: ObjectSchema = {
        name: 'Filter',
        properties: {
            _id: 'int',
            name: { type: 'string', indexed: 'full-text' }
        },
        primaryKey: '_id'
    };
}