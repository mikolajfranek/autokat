export default class Catalyst {
    constructor(id,
        model,
        brand,
        type,
        weight,
        platinum,
        palladium,
        rhodium,
        pictureId,
        pictureURL,
        thumbnail) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.weight = weight;
        this.platinum = platinum;
        this.palladium = palladium;
        this.rhodium = rhodium;
        this.pictureId = pictureId;
        this.pictureURL = pictureURL;
        this.thumbnail = thumbnail;
    }
}