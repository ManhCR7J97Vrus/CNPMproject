INSERT INTO storage_profiles (code, temperature_min, temperature_max, humidity_min, humidity_max,
                              avoid_light, fire_protection, incompatible_groups)
VALUES ('THUONG', NULL, NULL, NULL, NULL, FALSE, FALSE, ''),
       ('MAT', 2, 8, 40, 70, TRUE, FALSE, '');

INSERT INTO service_levels (priority, sort_rank)
VALUES ('URGENT', 1), ('EXPRESS', 2), ('NORMAL', 3);
