import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';

export default class CourseExchange extends Model {
    static table = 'courses_exchange';

    @readonly @date('created_at') createdAt;
    @text('mid') mid;
    @date('created_at') date;
    
    @text('usd_pln') usdPln;


    //TODO static?
    @writer static async add(eurPln, usdPln) {
        const newItem = await this.collections.get('courses_exchange').create(exchange => {
            exchange.eurPln.set(eurPln);
            exchange.usdPln.set(usdPln);
        });
        return newItem;
    }
}