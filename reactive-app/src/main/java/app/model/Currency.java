package app.model;

public enum Currency {
    DOLLAR {
        @Override
        public double in(Currency other) {
            return switch (other) {
                case RUB -> 76.7;
                case DOLLAR -> 1;
                case EURO -> 0.94;
            };
        }

        @Override
        public String toString() {
            return "dollar";
        }
    },
    EURO {
        @Override
        public double in(Currency other) {
            double dollarInEuro = DOLLAR.in(this);
            return switch (other) {
                case RUB -> DOLLAR.in(RUB) / dollarInEuro;
                case DOLLAR -> 1 / dollarInEuro;
                case EURO -> 1;
            };
        }

        @Override
        public String toString() {
            return "euro";
        }
    },
    RUB {
        @Override
        public double in(Currency other) {
            return switch (other) {
                case RUB -> 1;
                case DOLLAR -> 1 / DOLLAR.in(this);
                case EURO -> 1 / EURO.in(this);
            };
        }

        @Override
        public String toString() {
            return "rub";
        }
    };

    public static Currency forName(String name) {
        return Currency.valueOf(name.toUpperCase());
    }

    public abstract double in(Currency other);
}
