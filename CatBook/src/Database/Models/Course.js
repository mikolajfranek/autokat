import { Model } from '@nozbe/watermelondb'
import { date, readonly, field } from '@nozbe/watermelondb/decorators'

export default class Course extends Model {
    static table = 'courses';

    @readonly @date('created_at') createdAt;
    @field('platinum') platinum;
    @field('palladium') palladium;
    @field('rhodium') rhodium;
    @field('eur_pln') eurPln;
    @field('usd_pln') usdPln;

    get isRecentlyCreated() {
        // in the last 1 day
        return this.createdAt &&
            this.createdAt.getTime() > Date.now() - 1 * 24 * 3600 * 1000
    }
}