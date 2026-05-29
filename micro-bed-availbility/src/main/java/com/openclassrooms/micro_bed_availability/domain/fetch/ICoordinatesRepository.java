package com.openclassrooms.micro_bed_availability.domain.fetch;

import java.util.List;

public interface ICoordinatesRepository {
    Coordinates getCoordinates(String address);
}
