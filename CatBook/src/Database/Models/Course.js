import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';

export default class Course extends Model {
    static table = 'courses';

    @readonly @date('created_at') createdAt;
    @text('platinum') platinum;
    @text('palladium') palladium;
    @text('rhodium') rhodium;
    @text('eur_pln') eurPln;
    @text('usd_pln') usdPln;

    get isRecentlyCreated() {
        // in the last 4 hours
        return this.createdAt &&
            this.createdAt.getTime() > Date.now() - 1 * 4 * 3600 * 1000;
    }
}